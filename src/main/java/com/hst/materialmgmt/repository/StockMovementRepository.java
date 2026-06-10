package com.hst.materialmgmt.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.StockMovementEntity;
import com.hst.materialmgmt.repository.StockMovementRepository.KpiTotalsRow;
import com.hst.materialmgmt.repository.StockMovementRepository.MaterialStockRow;
import com.hst.materialmgmt.repository.StockMovementRepository.TrendRow;
import com.hst.materialmgmt.repository.StockMovementRepository.WastageRow;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.StockMovementRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class StockMovementRepository extends ParentRepositoryImpl {

    @Autowired private StockMovementRowMapper rowMapper;

    @Override protected String getTableName() { return "rm_stock_movement_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("movement_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) StockMovementEntity.class; }

    // ── Records ───────────────────────────────────────────────────────────────

    public record MaterialStockRow(
        String materialId, String materialName, String category, String uom,
        BigDecimal reorderLevel, BigDecimal safetyStockLevel,
        BigDecimal stockOnHand, BigDecimal inboundPeriod,
        BigDecimal consumedPeriod, BigDecimal wastagePeriod) {}

    public record KpiTotalsRow(
        BigDecimal totalInbound, BigDecimal totalConsumed,
        BigDecimal totalWastage, BigDecimal totalStockValue) {}

    public record TrendRow(LocalDate date, BigDecimal inbound,
        BigDecimal consumed, BigDecimal wastage) {}

    public record WastageRow(String reasonCode, BigDecimal totalQty, Long eventCount) {}

    // ── findFiltered ──────────────────────────────────────────────────────────

    public Flux<StockMovementEntity> findFiltered(
            String materialId, String movementType,
            LocalDate fromDate, LocalDate toDate) {
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM erp_finance_schema.rm_stock_movement_tbl WHERE 1=1");
        if (materialId   != null) sql.append(" AND material_id   = '").append(materialId).append("'");
        if (movementType != null) sql.append(" AND movement_type = '").append(movementType).append("'");
        if (fromDate     != null) sql.append(" AND movement_date >= '").append(fromDate).append("'");
        if (toDate       != null) sql.append(" AND movement_date <= '").append(toDate).append("'");
        sql.append(" ORDER BY movement_date DESC");
        return databaseClient.sql(sql.toString())
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    // ── findByReferenceId — fetch movements for a specific shift/GRN ─────────

    public Flux<StockMovementEntity> findByReferenceId(String referenceId, String movementType) {
        StringBuilder sql = new StringBuilder(
            "SELECT mv.*, m.material_name, m.uom AS mat_uom " +
            "FROM erp_finance_schema.rm_stock_movement_tbl mv " +
            "LEFT JOIN rm_material_schema.rm_material_tbl m ON m.material_id = mv.material_id " +
            "WHERE mv.reference_id = :refId");
        if (movementType != null)
            sql.append(" AND mv.movement_type = '").append(movementType).append("'");
        sql.append(" ORDER BY mv.created_at ASC");
        return databaseClient.sql(sql.toString())
                .bind("refId", referenceId)
                .map((row, meta) -> {
                    StockMovementEntity e = rowMapper.apply(row, meta);
                    // Attach material name as notes suffix if not already set
                    String matName = row.get("material_name", String.class);
                    if (matName != null && (e.getNotes() == null || !e.getNotes().contains(matName))) {
                        e.setNotes(matName); // reuse notes field to carry name to frontend
                    }
                    return e;
                }).all();
    }

    // ── updateMovementQty — edit consumption quantity ─────────────────────────

    public Mono<Void> updateMovementQty(String movementId, BigDecimal quantity) {
        return databaseClient.sql("""
            UPDATE erp_finance_schema.rm_stock_movement_tbl
            SET quantity = :qty, updated_at = NOW()
            WHERE movement_id = :id
            """)
                .bind("qty", quantity)
                .bind("id",  movementId)
                .fetch().rowsUpdated().then();
    }

    // ── deleteMovement — remove a single movement record ─────────────────────

    public Mono<Void> deleteMovement(String movementId) {
        return databaseClient.sql(
            "DELETE FROM erp_finance_schema.rm_stock_movement_tbl WHERE movement_id = :id")
                .bind("id", movementId)
                .fetch().rowsUpdated().then();
    }

    // ── findStockOnHandWithMaster ─────────────────────────────────────────────

    public Flux<MaterialStockRow> findStockOnHandWithMaster(LocalDate from, LocalDate to) {
        String sql = """
            SELECT m.material_id, m.material_name, m.category, m.uom,
                   m.reorder_level, m.safety_stock_level,
                   GREATEST(0,
                       COALESCE(SUM(CASE
                           WHEN mv.movement_type IN ('INBOUND','ADJUSTMENT') THEN mv.quantity
                           WHEN mv.movement_type IN ('CONSUMPTION','WASTAGE') THEN -mv.quantity
                           ELSE 0 END), 0)
                   ) AS stock_on_hand,
                   COALESCE(SUM(CASE WHEN mv.movement_type='INBOUND'
                                      AND mv.movement_date BETWEEN :from AND :to
                                     THEN mv.quantity ELSE 0 END), 0) AS inbound_period,
                   COALESCE(ABS(SUM(CASE WHEN mv.movement_type='CONSUMPTION'
                                          AND mv.movement_date BETWEEN :from AND :to
                                         THEN mv.quantity ELSE 0 END)), 0) AS consumed_period,
                   COALESCE(ABS(SUM(CASE WHEN mv.movement_type='WASTAGE'
                                          AND mv.movement_date BETWEEN :from AND :to
                                         THEN mv.quantity ELSE 0 END)), 0) AS wastage_period
            FROM rm_material_schema.rm_material_tbl m
            LEFT JOIN erp_finance_schema.rm_stock_movement_tbl mv ON mv.material_id = m.material_id
            WHERE m.is_active = TRUE
            GROUP BY m.material_id, m.material_name, m.category, m.uom,
                     m.reorder_level, m.safety_stock_level
            ORDER BY m.material_id
            """;
        return databaseClient.sql(sql).bind("from", from).bind("to", to)
                .map((row, meta) -> new MaterialStockRow(
                        row.get("material_id",        String.class),
                        row.get("material_name",      String.class),
                        row.get("category",           String.class),
                        row.get("uom",                String.class),
                        row.get("reorder_level",      BigDecimal.class),
                        row.get("safety_stock_level", BigDecimal.class),
                        row.get("stock_on_hand",      BigDecimal.class),
                        row.get("inbound_period",     BigDecimal.class),
                        row.get("consumed_period",    BigDecimal.class),
                        row.get("wastage_period",     BigDecimal.class))).all();
    }

    // ── findKpiTotals ─────────────────────────────────────────────────────────

    public Mono<KpiTotalsRow> findKpiTotals(LocalDate from, LocalDate to) {
        String sql = """
            SELECT
              COALESCE(SUM(CASE WHEN movement_type='INBOUND'     THEN quantity ELSE 0 END),0) AS total_inbound,
              COALESCE(ABS(SUM(CASE WHEN movement_type='CONSUMPTION' THEN quantity ELSE 0 END)),0) AS total_consumed,
              COALESCE(ABS(SUM(CASE WHEN movement_type='WASTAGE'  THEN quantity ELSE 0 END)),0) AS total_wastage,
              COALESCE(SUM(CASE WHEN movement_type='INBOUND' AND unit_cost IS NOT NULL
                                THEN quantity * unit_cost ELSE 0 END),0) AS total_stock_value
            FROM erp_finance_schema.rm_stock_movement_tbl
            WHERE movement_date BETWEEN :from AND :to
            """;
        return databaseClient.sql(sql).bind("from", from).bind("to", to)
                .map((row, meta) -> new KpiTotalsRow(
                        row.get("total_inbound",     BigDecimal.class),
                        row.get("total_consumed",    BigDecimal.class),
                        row.get("total_wastage",     BigDecimal.class),
                        row.get("total_stock_value", BigDecimal.class))).one();
    }

    // ── findDailyTrend ────────────────────────────────────────────────────────

    public Flux<TrendRow> findDailyTrend(int days) {
        String sql = """
            SELECT movement_date AS date,
              COALESCE(SUM(CASE WHEN movement_type='INBOUND'     THEN quantity ELSE 0 END),0) AS inbound,
              COALESCE(ABS(SUM(CASE WHEN movement_type='CONSUMPTION' THEN quantity ELSE 0 END)),0) AS consumed,
              COALESCE(ABS(SUM(CASE WHEN movement_type='WASTAGE'  THEN quantity ELSE 0 END)),0) AS wastage
            FROM erp_finance_schema.rm_stock_movement_tbl
            WHERE movement_date >= CURRENT_DATE - CAST(:days AS INT) * INTERVAL '1 day'
            GROUP BY movement_date ORDER BY movement_date
            """;
        return databaseClient.sql(sql).bind("days", days)
                .map((row, meta) -> new TrendRow(
                        row.get("date",    LocalDate.class),
                        row.get("inbound", BigDecimal.class),
                        row.get("consumed",BigDecimal.class),
                        row.get("wastage", BigDecimal.class))).all();
    }

    // ── findWastageBreakdown ──────────────────────────────────────────────────

    public Flux<WastageRow> findWastageBreakdown(int days) {
        String sql = """
            SELECT COALESCE(reason_code,'OTHER') AS reason_code,
                   SUM(quantity) AS total_qty, COUNT(*) AS event_count
            FROM erp_finance_schema.rm_stock_movement_tbl
            WHERE movement_type='WASTAGE'
              AND movement_date >= CURRENT_DATE - CAST(:days AS INT) * INTERVAL '1 day'
            GROUP BY reason_code ORDER BY total_qty DESC
            """;
        return databaseClient.sql(sql).bind("days", days)
                .map((row, meta) -> new WastageRow(
                        row.get("reason_code", String.class),
                        row.get("total_qty",   BigDecimal.class),
                        row.get("event_count", Long.class))).all();
    }

    // ── createEntity ──────────────────────────────────────────────────────────

    public Mono<Void> createEntity(StockMovementEntity e) {
        return databaseClient.sql("""
            INSERT INTO erp_finance_schema.rm_stock_movement_tbl
              (movement_id, material_id, movement_type, quantity,
               movement_date, reason_code, notes, created_at, updated_at)
            VALUES (:id, :matId, :type, :qty, :date, :reason, :notes, NOW(), NOW())
            """)
                .bind("id",     e.getMovementId())
                .bind("matId",  e.getMaterialId())
                .bind("type",   e.getMovementType())
                .bind("qty",    e.getQuantity())
                .bind("date",   e.getMovementDate())
                .bind("reason", e.getReasonCode() != null ? e.getReasonCode() : "")
                .bind("notes",  e.getNotes()      != null ? e.getNotes()      : "")
                .fetch().rowsUpdated().then();
    }

    // ── deleteByReferenceId ───────────────────────────────────────────────────

    public Mono<Void> deleteByReferenceId(String referenceId) {
        return databaseClient.sql(
            "DELETE FROM erp_finance_schema.rm_stock_movement_tbl WHERE reference_id = :refId")
                .bind("refId", referenceId)
                .fetch().rowsUpdated().then();
    }

    // ── resetAllStock ─────────────────────────────────────────────────────────

    public Mono<Void> resetAllStock() {
        return databaseClient.sql(
            "TRUNCATE TABLE erp_finance_schema.rm_stock_movement_tbl RESTART IDENTITY CASCADE")
                .fetch().rowsUpdated()
                .then(databaseClient.sql(
                    "TRUNCATE TABLE rm_material_schema.rm_stock_movement_tbl RESTART IDENTITY CASCADE")
                        .fetch().rowsUpdated())
                .then();
    }
}