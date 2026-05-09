package com.hst.materialmgmt.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hs.api.model.DashboardSummary;
import com.hs.api.model.MaterialStockSummary;
import com.hs.api.model.StockMovement;
import com.hs.api.model.TrendPoint;
import com.hs.api.model.WastageReason;
import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.StockMovementMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.StockMovementRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Stock movement service — handles single-row CRUD plus aggregation queries
 * for the inventory dashboard.
 */
@Service
public class StockMovementService extends ParentBaseServiceImpl {

    @Autowired private StockMovementRepository repository;
    @Autowired private StockMovementMapper mapper;
    @Autowired private MovementCodeGenerator codeGenerator;

    @Override protected BaseMapper getMapper() { return mapper; }
    @Override protected ParentRepositoryImpl getParentRepository() { return repository; }

    /**
     * Override create to inject server-generated movement code.
     */
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

    // ─── Batch insert (multiple movements in one call) ─────────────
    // Goes through createFullHierarchy so each row gets a code AND uses the
    // existing persistence path (no direct save() call).

    public Flux<StockMovement> createBatch(List<StockMovement> movements) {
        return Flux.fromIterable(movements)
                .concatMap(m -> createFullHierarchy(Mono.just((Object) m))
                        .cast(StockMovement.class));
    }

    // ─── Dashboard aggregations ────────────────────────────────────

    public Mono<DashboardSummary> getDashboardSummary(LocalDate fromDate, LocalDate toDate) {

        Mono<List<MaterialStockSummary>> breakdownMono = repository
                .findStockOnHandWithMaster(fromDate, toDate)
                .map(this::toMaterialStockSummary)
                .collectList();

        return Mono.zip(repository.findKpiTotals(fromDate, toDate), breakdownMono)
                .map(tuple -> {
                    var totals = tuple.getT1();
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