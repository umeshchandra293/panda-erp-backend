package com.hst.materialmgmt.service.fgstock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import com.hst.materialmgmt.entity.fgstock.*;
import com.hst.materialmgmt.repository.fgstock.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FgStockService {

    @Autowired private FgStockRepository        stockRepo;
    @Autowired private FgMovementRepository     movementRepo;
    @Autowired private FgDispatchRepository     dispatchRepo;
    @Autowired private FgDispatchItemRepository dispatchItemRepo;
    @Autowired private DatabaseClient           db;
    @Autowired private TransactionalOperator    tx;

    // ── Stock + Ledger ────────────────────────────────────────────────────────

    public Flux<FgStockEntity> getAllStock() {
        return db.sql("""
            SELECT f.*, p.name AS product_name, p.sku, p.uom,
                   p.units_per_box,
                   f.quantity / NULLIF(p.units_per_box, 0) AS full_boxes,
                   MOD(f.quantity, NULLIF(p.units_per_box, 1)) AS loose_units
            FROM rm_material_schema.fg_stock_tbl f
            LEFT JOIN rm_material_schema.product_tbl p ON p.product_id = f.product_id
            """)
            .map((row, meta) -> {
                FgStockEntity e = new FgStockEntity();
                e.setFgId(row.get("fg_id",            String.class));
                e.setProductId(row.get("product_id",  String.class));
                e.setProductName(row.get("product_name", String.class));
                e.setSku(row.get("sku",               String.class));
                e.setUnit(row.get("uom",              String.class));
                Integer qty = row.get("quantity", Integer.class);
                e.setQuantity(qty != null ? qty : 0);
                Integer upb = row.get("units_per_box", Integer.class);
                e.setUnitsPerBox(upb != null ? upb : 1);
                Integer fb = row.get("full_boxes", Integer.class);
                e.setFullBoxes(fb != null ? fb : 0);
                Integer lu = row.get("loose_units", Integer.class);
                e.setLooseUnits(lu != null ? lu : 0);
                return e;
            }).all();
    }

    public Flux<FgMovementEntity> getLedger(String productId, String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT m.*, p.name AS product_name
            FROM rm_material_schema.fg_movement_tbl m
            LEFT JOIN rm_material_schema.product_tbl p ON p.product_id = m.product_id
            WHERE 1=1
            """);
        if (productId != null && !productId.isBlank())
            sql.append(" AND m.product_id = '").append(productId).append("'");
        if (fromDate != null && !fromDate.isBlank())
            sql.append(" AND m.movement_date >= '").append(fromDate).append("'");
        if (toDate != null && !toDate.isBlank())
            sql.append(" AND m.movement_date <= '").append(toDate).append("'");
        sql.append(" ORDER BY m.movement_date DESC");

        return db.sql(sql.toString())
            .map((row, meta) -> {
                FgMovementEntity e = new FgMovementEntity();
                e.setMovementId(row.get("movement_id",   String.class));
                e.setProductId(row.get("product_id",     String.class));
                e.setProductName(row.get("product_name", String.class));
                e.setMovementType(row.get("movement_type", String.class));
                Integer qty = row.get("quantity", Integer.class);
                e.setQuantity(qty != null ? qty : 0);
                e.setReferenceId(row.get("reference_id", String.class));
                e.setMovementDate(row.get("movement_date", LocalDate.class));
                e.setNotes(row.get("notes",              String.class));
                return e;
            }).all();
    }

    // ── Production ────────────────────────────────────────────────────────────

    public Mono<Void> addStock(String productId, int qty, String shiftId) {
        if (qty <= 0) return Mono.empty();
        FgMovementEntity mv = new FgMovementEntity();
        mv.setMovementId("FGM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        mv.setProductId(productId);
        mv.setMovementType("PRODUCED");
        mv.setQuantity(qty);
        mv.setReferenceId(shiftId);
        mv.setMovementDate(LocalDate.now());
        mv.setNotes("Shift production: " + shiftId);
        return movementRepo.create(mv, FgMovementEntity.class)
                .then(stockRepo.upsertStock(productId, qty));
    }

    // ── Dispatch (new multi-product with driver details) ──────────────────────

    public record DispatchItemRequest(
        String productId,
        int    casesDispatched,
        int    bottlesDispatched,
        double sellingPricePerCase
    ) {}

    public record DispatchRequest(
        String                  dispatchDate,
        String                  driverName,
        String                  driverPhone,
        String                  vehicleNumber,
        String                  deliveryOrder,
        String                  destination,
        String                  notes,
        double                  amountToCollect,
        List<DispatchItemRequest> items
    ) {}

    public Mono<FgDispatchEntity> createDispatch(DispatchRequest req) {
        return dispatchRepo.nextDispatchId()
            .flatMap(dispatchId -> {
                // Build dispatch header
                FgDispatchEntity header = new FgDispatchEntity();
                header.setDispatchId(dispatchId);
                header.setDispatchDate(req.dispatchDate() != null
                        ? LocalDate.parse(req.dispatchDate()) : LocalDate.now());
                header.setDriverName(req.driverName());
                header.setDriverPhone(req.driverPhone());
                header.setVehicleNumber(req.vehicleNumber());
                header.setDeliveryOrder(req.deliveryOrder());
                header.setDestination(req.destination());
                header.setNotes(req.notes());
                header.setStatus("DISPATCHED");
                header.setAmountToCollect(BigDecimal.valueOf(req.amountToCollect()));
                header.setAmountCollected(BigDecimal.ZERO);

                return dispatchRepo.create(header, FgDispatchEntity.class)
                    .cast(FgDispatchEntity.class)
                    .flatMap(saved -> {
                        // For each item: create dispatch item + deduct stock + FG movement
                        List<DispatchItemRequest> items = req.items() != null
                                ? req.items() : List.of();

                        return Flux.fromIterable(items)
                            .filter(i -> i.productId() != null && i.casesDispatched() > 0)
                            .concatMap(i -> saveDispatchItem(saved.getDispatchId(), i))
                            .then(Mono.just(saved));
                    });
            })
            .as(tx::transactional);
    }

    private Mono<Void> saveDispatchItem(String dispatchId, DispatchItemRequest item) {
        return dispatchItemRepo.nextItemId().flatMap(itemId -> {
            FgDispatchItemEntity de = new FgDispatchItemEntity();
            de.setDispatchItemId(itemId);
            de.setDispatchId(dispatchId);
            de.setProductId(item.productId());
            de.setCasesDispatched(item.casesDispatched());
            de.setBottlesDispatched(item.bottlesDispatched());
            de.setCasesReturned(0);
            de.setBottlesReturned(0);
            de.setSellingPricePerCase(BigDecimal.valueOf(item.sellingPricePerCase()));

            return dispatchItemRepo.create(de, FgDispatchItemEntity.class)
                .then(stockRepo.upsertStock(item.productId(), -item.bottlesDispatched()))
                .then(Mono.defer(() -> {
                    FgMovementEntity mv = new FgMovementEntity();
                    mv.setMovementId("FGM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    mv.setProductId(item.productId());
                    mv.setMovementType("DISPATCHED");
                    mv.setQuantity(item.bottlesDispatched());
                    mv.setReferenceId(dispatchId);
                    mv.setMovementDate(LocalDate.now());
                    mv.setNotes("Dispatch: " + dispatchId);
                    return movementRepo.create(mv, FgMovementEntity.class).then();
                }));
        });
    }

    // ── Legacy single-product dispatch (backward compat) ─────────────────────

    public Mono<Void> dispatch(String productId, int qty, String referenceId, String notes) {
        return stockRepo.upsertStock(productId, -qty)
            .then(Mono.defer(() -> {
                FgMovementEntity mv = new FgMovementEntity();
                mv.setMovementId("FGM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                mv.setProductId(productId);
                mv.setMovementType("DISPATCHED");
                mv.setQuantity(qty);
                mv.setReferenceId(referenceId);
                mv.setMovementDate(LocalDate.now());
                mv.setNotes(notes);
                return movementRepo.create(mv, FgMovementEntity.class).then();
            }));
    }

    // ── Dispatch list + items ─────────────────────────────────────────────────

    public Flux<FgDispatchEntity> getAllDispatches() {
        return dispatchRepo.findAllDispatches();
    }

    public Flux<FgDispatchItemEntity> getDispatchItems(String dispatchId) {
        return dispatchItemRepo.findByDispatchId(dispatchId);
    }

    // ── Settle ────────────────────────────────────────────────────────────────

    public record SettleItemRequest(
        String dispatchItemId,
        int    casesReturned,
        int    bottlesReturned
    ) {}

    public record SettleRequest(
        double                  amountCollected,
        String                  paymentMode,
        String                  notes,
        List<SettleItemRequest> itemReturns
    ) {}

    public Mono<Void> settle(String dispatchId, SettleRequest req) {
        return dispatchRepo.findDispatchById(dispatchId)
            .switchIfEmpty(Mono.error(new RuntimeException("Dispatch not found: " + dispatchId)))
            .flatMap(dispatch -> {
                // Update header
                Mono<Void> updateHeader = dispatchRepo.settle(
                    dispatchId,
                    BigDecimal.valueOf(req.amountCollected()),
                    req.paymentMode(),
                    req.notes()
                );

                // Process returns
                List<SettleItemRequest> returns = req.itemReturns() != null
                        ? req.itemReturns() : List.of();

                Mono<Void> processReturns = Flux.fromIterable(returns)
                    .filter(r -> r.bottlesReturned() > 0)
                    .concatMap(r -> processReturn(dispatchId, r))
                    .then();

                return updateHeader.then(processReturns);
            })
            .as(tx::transactional);
    }

    private Mono<Void> processReturn(String dispatchId, SettleItemRequest r) {
        return dispatchItemRepo.updateReturns(
                r.dispatchItemId(), r.casesReturned(), r.bottlesReturned())
            .then(dispatchItemRepo.findByItemId(r.dispatchItemId())
                .flatMap(item -> {
                    // Return stock
                    return stockRepo.upsertStock(item.getProductId(), r.bottlesReturned())
                        .then(Mono.defer(() -> {
                            // Return movement
                            FgMovementEntity mv = new FgMovementEntity();
                            mv.setMovementId("FGM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                            mv.setProductId(item.getProductId());
                            mv.setMovementType("RETURNED");
                            mv.setQuantity(r.bottlesReturned());
                            mv.setReferenceId(dispatchId);
                            mv.setMovementDate(LocalDate.now());
                            mv.setNotes("Return from dispatch: " + dispatchId);
                            return movementRepo.create(mv, FgMovementEntity.class).then();
                        }));
                }));
    }
    }
