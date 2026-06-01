package com.hst.materialmgmt.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hst.api.model.DashboardSummary;
import com.hst.api.model.MaterialStockSummary;
import com.hst.api.model.StockMovement;
import com.hst.api.model.TrendPoint;
import com.hst.api.model.WastageReason;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.StockMovementMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.StockMovementRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockMovementService extends ParentBaseServiceImpl {

    @Autowired private StockMovementRepository repository;
    @Autowired private StockMovementMapper mapper;
    @Autowired private MovementCodeGenerator codeGenerator;

    @Override protected BaseMapper getMapper() { return mapper; }
    @Override protected ParentRepositoryImpl getParentRepository() { return repository; }

    @Override
    public Mono<Object> createFullHierarchy(Mono<Object> parentMono) {
        if (parentMono == null) {
            return Mono.error(new IllegalArgumentException("Movement cannot be null"));
        }
        Mono<Object> codedMono = parentMono.flatMap(model -> {
            StockMovement m = (StockMovement) model;
            return codeGenerator.nextMovementCode()
                    .map(code -> { m.setMovementId(code); return (Object) m; });
        });
        return super.createFullHierarchy(codedMono);
    }

    // ─── Filter / list ─────────────────────────────────────────────

    public Flux<StockMovement> findFiltered(
            String materialId, String movementType,
            LocalDate fromDate, LocalDate toDate) {
        return repository.findFiltered(materialId, movementType, fromDate, toDate)
                .map(entity -> (StockMovement) mapper.toModel(entity));
    }

    // ─── Batch insert with negative stock guard ────────────────────
    // For WASTAGE and CONSUMPTION: check current stock >= quantity before saving.
    // For INBOUND and ADJUSTMENT: no stock check needed.

    public Flux<StockMovement> createBatch(List<StockMovement> movements) {
        return Flux.fromIterable(movements)
                .concatMap(m -> validateMovement(m)
                        .then(createFullHierarchy(Mono.just((Object) m)))
                        .cast(StockMovement.class));
    }

    // ─── Validate: prevent negative stock ─────────────────────────

    private Mono<Void> validateMovement(StockMovement m) {
        // Only outbound movements can cause negative stock
        if (m.getMovementType() == null) return Mono.empty();
        String type = m.getMovementType().name(); // enum.name() = "WASTAGE", "CONSUMPTION" etc

        if (!type.equals("WASTAGE") && !type.equals("CONSUMPTION")) {
            return Mono.empty();
        }

        if (m.getMaterialId() == null || m.getMaterialId().isBlank()) {
            return Mono.error(new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "materialId is required"));
        }

        // quantity is Double in the generated model
        double requested = m.getQuantity() != null ? m.getQuantity() : 0.0;
        if (requested <= 0) {
            return Mono.error(new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "quantity must be > 0"));
        }

        // Get current stock on hand from DB and check sufficiency
        return repository.findStockOnHandWithMaster(
                LocalDate.of(2000, 1, 1), LocalDate.now())
            .filter(row -> m.getMaterialId().equals(row.materialId()))
            .next()
            .flatMap(row -> {
                double currentStock = row.stockOnHand() != null
                    ? row.stockOnHand().doubleValue() : 0.0;
                if (requested > currentStock) {
                    return Mono.error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format(
                            "Insufficient stock for %s — available: %.2f, requested: %.2f",
                            m.getMaterialId(), currentStock, requested)));
                }
                return Mono.<Void>empty();
            })
            .then();
    }

    // ─── Dashboard aggregations ────────────────────────────────────

    public Mono<DashboardSummary> getDashboardSummary(LocalDate fromDate, LocalDate toDate) {
        Mono<List<MaterialStockSummary>> breakdownMono = repository
                .findStockOnHandWithMaster(fromDate, toDate)
                .map(this::toMaterialStockSummary)
                .collectList();

        return Mono.zip(repository.findKpiTotals(fromDate, toDate), breakdownMono)
                .map(tuple -> {
                    var totals   = tuple.getT1();
                    var breakdown = tuple.getT2();
                    DashboardSummary s = new DashboardSummary();
                    s.setTotalInbound(safeDouble(totals.totalInbound()));
                    s.setTotalConsumed(safeDouble(totals.totalConsumed()));
                    s.setTotalWastage(safeDouble(totals.totalWastage()));
                    s.setTotalStockValue(safeDouble(totals.totalStockValue()));
                    s.setMaterialBreakdown(breakdown);
                    return s;
                });
    }

    public Flux<TrendPoint> getTrend(int days) {
        return repository.findDailyTrend(days)
                .map(row -> {
                    TrendPoint p = new TrendPoint();
                    p.setDate(row.date());
                    p.setInbound(safeDouble(row.inbound()));
                    p.setConsumed(safeDouble(row.consumed()));
                    p.setWastage(safeDouble(row.wastage()));
                    return p;
                });
    }

    public Flux<WastageReason> getWastageBreakdown(int days) {
        return repository.findWastageBreakdown(days)
                .map(row -> {
                    WastageReason w = new WastageReason();
                    w.setReasonCode(row.reasonCode());
                    w.setTotalQty(safeDouble(row.totalQty()));
                    w.setEventCount(row.eventCount() != null ? row.eventCount().intValue() : 0);
                    return w;
                });
    }

    // ─── Helpers ───────────────────────────────────────────────────

    private MaterialStockSummary toMaterialStockSummary(StockMovementRepository.MaterialStockRow r) {
        MaterialStockSummary s = new MaterialStockSummary();
        s.setMaterialId(r.materialId());
        s.setMaterialName(r.materialName());
        s.setCategory(r.category());
        s.setUom(r.uom());
        s.setStockOnHand(safeDouble(r.stockOnHand()));
        s.setInboundPeriod(safeDouble(r.inboundPeriod()));
        s.setConsumedPeriod(safeDouble(r.consumedPeriod()));
        s.setWastagePeriod(safeDouble(r.wastagePeriod()));
        s.setReorderLevel(safeDouble(r.reorderLevel()));
        s.setSafetyStockLevel(safeDouble(r.safetyStockLevel()));
        Double stock = s.getStockOnHand();
        if (stock != null) {
            s.setBelowReorder(s.getReorderLevel() != null && stock < s.getReorderLevel());
            s.setBelowSafety(s.getSafetyStockLevel() != null && stock < s.getSafetyStockLevel());
        }
        return s;
    }

    private static Double safeDouble(java.math.BigDecimal bd) {
        return bd == null ? 0.0 : bd.doubleValue();
    }
}