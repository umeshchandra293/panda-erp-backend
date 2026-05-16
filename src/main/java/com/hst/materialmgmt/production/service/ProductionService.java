package com.hst.materialmgmt.production.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.api.model.ProductionBatch;
import com.hst.api.model.ProductionShift;
import com.hst.api.model.ProductionShiftRequest;
import com.hst.api.model.FgStockItem;
import com.hst.materialmgmt.production.dto.FgDispatchRequest;
import com.hst.materialmgmt.production.dto.FgMovement;      
import com.hst.materialmgmt.production.entity.*;
import com.hst.materialmgmt.production.repository.*;
import com.hst.materialmgmt.repository.StockMovementRepository;
import com.hst.materialmgmt.entity.StockMovementEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductionService {

    @Autowired private ProductionShiftRepository shiftRepo;
    @Autowired private ProductionBatchRepository batchRepo;
    @Autowired private ProductionBomRepository   bomRepo;
    @Autowired private FgStockRepository         fgStockRepo;
    @Autowired private StockMovementRepository   rmMovementRepo;

    // ── Get all shifts ─────────────────────────────────────────────────

    public Flux<ProductionShift> getShifts(LocalDate from, LocalDate to) {
        LocalDate f = from != null ? from : LocalDate.now().minusDays(30);
        LocalDate t = to   != null ? to   : LocalDate.now();
        return shiftRepo.findByDateRange(f, t)
                .flatMap(shift -> batchRepo.findByShiftId(shift.getShiftId())
                        .map(this::toBatch).collectList()
                        .map(batches -> toShift(shift, batches)));
    }

    // ── Get FG stock ───────────────────────────────────────────────────

    public Flux<FgStockItem> getFgStock() {
        return fgStockRepo.findAllFgStock().map(this::toFgItem);
    }

    // ── Dispatch FG ────────────────────────────────────────────────────

    public Mono<FgStockItem> dispatchFg(FgDispatchRequest req) {
        BigDecimal qty = BigDecimal.valueOf(req.getQuantity());
        return fgStockRepo.nextFgMovementId()
                .flatMap(movId -> fgStockRepo.insertDispatchMovement(
                        movId,
                        req.getProductId(),
                        qty,
                        req.getReferenceId(),
                        req.getNotes(),
                        LocalDate.now()))
                .then(fgStockRepo.subtractQuantity(req.getProductId(), qty))
                .then(fgStockRepo.findByProductId(req.getProductId()))
                .map(this::toFgItem);
    }

    // ── Get FG Ledger ──────────────────────────────────────────────────

    public Flux<FgMovement> getFgLedger(String productId, LocalDate from, LocalDate to) {
        return fgStockRepo.findLedger(productId, from, to)
                .map(e -> {
                    FgMovement m = new FgMovement();
                    m.setMovementId(e.getMovementId());
                    m.setProductId(e.getProductId());
                    m.setMovementType(e.getMovementType() != null
                            ? FgMovement.MovementTypeEnum.fromValue(e.getMovementType())
                            : null);
                    if (e.getQuantity() != null)
                        m.setQuantity(e.getQuantity().doubleValue());
                    m.setReferenceType(e.getReferenceType());
                    m.setReferenceId(e.getReferenceId());
                    m.setMovementDate(e.getMovementDate());
                    m.setNotes(e.getNotes());
                    return m;
                });
    }

    // ── Create shift with batches ──────────────────────────────────────
    // Flow per batch:
    //   1. Save batch row
    //   2. Load BOM for product
    //   3. For each BOM line: create CONSUMPTION stock movement (RM goes down)
    //   4. Create FG stock movement (FG goes up)
    //   5. Update fg_stock_tbl quantity

    public Mono<ProductionShift> createShift(ProductionShiftRequest req) {
        return shiftRepo.nextShiftId().flatMap(shiftId -> {

            ProductionShiftEntity shift = new ProductionShiftEntity();
            shift.setShiftId(shiftId);
            shift.setShiftDate(req.getShiftDate() != null
                    ? req.getShiftDate() : LocalDate.now());
            shift.setShiftType(req.getShiftType().name());
            shift.setOperatorName(req.getOperatorName());
            shift.setNotes(req.getNotes());
            shift.setStatus("CONFIRMED");

            return shiftRepo.create(shift, ProductionShiftEntity.class)
                    .flatMap(savedShift ->
                            Flux.fromIterable(req.getBatches() != null
                                    ? req.getBatches()
                                    : List.<com.hst.api.model.BatchEntry>of())
                                    .concatMap(entry -> processBatch(shiftId,
                                            shift.getShiftDate(), entry))
                                    .collectList()
                                    .map(batches -> toShift(
                                            (ProductionShiftEntity) savedShift, batches)));
        });
    }

    private Mono<ProductionBatch> processBatch(
            String shiftId, LocalDate shiftDate,
            com.hst.api.model.BatchEntry entry) {

        return batchRepo.nextBatchId().flatMap(batchId -> {

            ProductionBatchEntity batch = new ProductionBatchEntity();
            batch.setBatchId(batchId);
            batch.setShiftId(shiftId);
            batch.setProductId(entry.getProductId());
            batch.setPlannedQty(entry.getPlannedQty() != null
                    ? BigDecimal.valueOf(entry.getPlannedQty()) : BigDecimal.ZERO);
            batch.setActualQty(entry.getActualQty() != null
                    ? BigDecimal.valueOf(entry.getActualQty()) : BigDecimal.ZERO);
            batch.setRejectedQty(entry.getRejectedQty() != null
                    ? BigDecimal.valueOf(entry.getRejectedQty()) : BigDecimal.ZERO);
            batch.setNotes(entry.getNotes());

            BigDecimal actualQty = batch.getActualQty();

            return batchRepo.create(batch, ProductionBatchEntity.class)
                    .cast(ProductionBatchEntity.class)
                    .flatMap(savedBatch ->
                            // Load BOM and create consumption movements
                            bomRepo.findByProductId(entry.getProductId())
                                    .concatMap(bom -> createConsumptionMovement(
                                            bom, actualQty, shiftId, shiftDate))
                                    .then()
                                    // Create FG movement + update stock
                                    .then(createFgMovement(
                                            entry.getProductId(), actualQty,
                                            shiftId, shiftDate))
                                    .thenReturn(toBatch(savedBatch)));
        });
    }

    /** Creates CONSUMPTION movement in rm_stock_movement_tbl */
    private Mono<Void> createConsumptionMovement(
            ProductionBomEntity bom, BigDecimal actualQty,
            String shiftId, LocalDate shiftDate) {

        BigDecimal consumed = bom.getQtyPerUnit().multiply(actualQty);
        String movId = "MOV-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();

        StockMovementEntity mv = new StockMovementEntity();
        mv.setMovementId(movId);
        mv.setMaterialId(bom.getMaterialId());
        mv.setMovementType("CONSUMPTION");
        // Constraint chk_qty_sign requires CONSUMPTION to be negative
        mv.setQuantity(consumed.negate());
        mv.setMovementDate(shiftDate);
        mv.setReferenceType("PRODUCTION");
        mv.setReferenceId(shiftId);
        mv.setNotes("Production consumption for shift: " + shiftId);

        return rmMovementRepo.create(mv, StockMovementEntity.class).then();
    }

    /** Creates PRODUCED movement in fg_stock_movement_tbl + updates fg_stock_tbl */
    private Mono<Void> createFgMovement(
            String productId, BigDecimal qty,
            String shiftId, LocalDate shiftDate) {

        return fgStockRepo.nextFgMovementId()
                .flatMap(movId -> fgStockRepo.insertFgMovement(
                        movId, productId, qty, shiftId, shiftDate)
                        .then(fgStockRepo.addQuantity(productId, qty)));
    }

    // ── Converters ─────────────────────────────────────────────────────

    private ProductionShift toShift(ProductionShiftEntity e,
            List<ProductionBatch> batches) {
        ProductionShift s = new ProductionShift();
        s.setShiftId(e.getShiftId());
        s.setShiftDate(e.getShiftDate());
        s.setShiftType(e.getShiftType() != null
                ? ProductionShift.ShiftTypeEnum.fromValue(e.getShiftType()) : null);
        s.setOperatorName(e.getOperatorName());
        s.setNotes(e.getNotes());
        s.setStatus(e.getStatus());
        s.setBatches(batches);
        double total = batches.stream()
                .mapToDouble(b -> b.getActualQty() != null ? b.getActualQty() : 0)
                .sum();
        s.setTotalUnitsProduced(total);
        return s;
    }

    private ProductionBatch toBatch(ProductionBatchEntity e) {
        ProductionBatch b = new ProductionBatch();
        b.setBatchId(e.getBatchId());
        b.setShiftId(e.getShiftId());
        b.setProductId(e.getProductId());
        if (e.getPlannedQty()  != null) b.setPlannedQty(e.getPlannedQty().doubleValue());
        if (e.getActualQty()   != null) b.setActualQty(e.getActualQty().doubleValue());
        if (e.getRejectedQty() != null) b.setRejectedQty(e.getRejectedQty().doubleValue());
        b.setNotes(e.getNotes());
        return b;
    }

    private FgStockItem toFgItem(FgStockEntity e) {
        FgStockItem i = new FgStockItem();
        i.setFgId(e.getFgId());
        i.setProductId(e.getProductId());
        i.setQuantity(e.getQuantity() != null ? e.getQuantity().doubleValue() : 0.0);
        return i;
    }
}
