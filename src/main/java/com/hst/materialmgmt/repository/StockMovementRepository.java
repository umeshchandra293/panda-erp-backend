package com.hst.materialmgmt.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.StockMovementEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.StockMovementRowMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Stock movement repository — extends base for vanilla CRUD,
 * adds aggregation queries for the dashboard.
 */
@Repository
public class StockMovementRepository extends ParentRepositoryImpl {

    @Autowired
    private StockMovementRowMapper rowMapper;

    @Override
    protected String getTableName() {
        return "rm_stock_movement_tbl";
    }

    @Override
    protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("movement_id", id);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> BaseRowMapper<T> getRowMapper() {
        return (BaseRowMapper<T>) rowMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Class<T> getEntityClass() {
        return (Class<T>) StockMovementEntity.class;
    }

    // ─── Filtered list query ───────────────────────────────────────

    public Flux<StockMovementEntity> findFiltered(
            String materialId, String movementType, LocalDate fromDate, LocalDate toDate) {

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM " + getTableNameWithQualifier() + " WHERE 1=1");
        Map<String, Object> binds = new HashMap<>();

        if (materialId != null && !materialId.isBlank()) {
            sql.append(" AND material_id = :materialId");
            binds.put("materialId", materialId);
        }
        if (movementType != null && !movementType.isBlank()) {
            sql.append(" AND movement_type = :movementType");
            binds.put("movementType", movementType);
        }
        if (fromDate != null) {
            sql.append(" AND movement_date >= :fromDate");
            binds.put("fromDate", fromDate);
        }
        if (toDate != null) {
            sql.append(" AND movement_date <= :toDate");
            binds.put("toDate", toDate);
        }
        sql.append(" ORDER BY movement_date DESC, movement_id DESC");

        var spec = databaseClient.sql(sql.toString());
        for (var e : binds.entrySet()) {
            spec = spec.bind(e.getKey(), e.getValue());
        }
        return spec.map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    // ─── Stock on hand per material (joined with material master) ──

    public Flux<MaterialStockRow> findStockOnHandWithMaster(LocalDate fromDate, LocalDate toDate) {
        String sql = """
            SELECT
                m.material_id,
                m.material_name,
                m.category,
                m.uom,
                m.reorder_level,
                m.safety_stock_level,
                COALESCE(SUM(mv.quantity), 0)                                                  AS stock_on_hand,
                COALESCE(SUM(CASE WHEN mv.movement_type = 'INBOUND'
                                   AND mv.movement_date BETWEEN :fromDate AND :toDate
                                  THEN mv.quantity ELSE 0 END), 0)                              AS inbound_period,
                COALESCE(ABS(SUM(CASE WHEN mv.movement_type = 'CONSUMPTION'
                                       AND mv.movement_date BETWEEN :fromDate AND :toDate
                                      THEN mv.quantity ELSE 0 END)), 0)                        AS consumed_period,
                COALESCE(ABS(SUM(CASE WHEN mv.movement_type = 'WASTAGE'
                                       AND mv.movement_date BETWEEN :fromDate AND :toDate
                                      THEN mv.quantity ELSE 0 END)), 0)                        AS wastage_period
            FROM erp_finance_schema.rm_material_tbl m
            LEFT JOIN erp_finance_schema.rm_stock_movement_tbl mv
                   ON mv.material_id = m.material_id
            WHERE m.is_active = TRUE
            GROUP BY m.material_id, m.material_name, m.category, m.uom,
                     m.reorder_level, m.safety_stock_level
            ORDER BY m.material_id
            """;

        return databaseClient.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map((row, meta) -> new MaterialStockRow(
                        row.get("material_id", String.class),
                        row.get("material_name", String.class),
                        row.get("category", String.class),
                        row.get("uom", String.class),
                        row.get("reorder_level", BigDecimal.class),
                        row.get("safety_stock_level", BigDecimal.class),
                        row.get("stock_on_hand", BigDecimal.class),
                        row.get("inbound_period", BigDecimal.class),
                        row.get("consumed_period", BigDecimal.class),
                        row.get("wastage_period", BigDecimal.class)))
                .all();
    }

    // ─── KPI totals across all materials in a date range ───────────

    public Mono<KpiTotals> findKpiTotals(LocalDate fromDate, LocalDate toDate) {
        String sql = """
            SELECT
                COALESCE(SUM(CASE WHEN movement_type = 'INBOUND'     THEN quantity ELSE 0 END), 0) AS total_inbound,
                COALESCE(ABS(SUM(CASE WHEN movement_type = 'CONSUMPTION' THEN quantity ELSE 0 END)), 0) AS total_consumed,
                COALESCE(ABS(SUM(CASE WHEN movement_type = 'WASTAGE'     THEN quantity ELSE 0 END)), 0) AS total_wastage,
                COALESCE(SUM(CASE WHEN movement_type = 'INBOUND' AND unit_cost IS NOT NULL
                                  THEN quantity * unit_cost ELSE 0 END), 0) AS total_stock_value
            FROM erp_finance_schema.rm_stock_movement_tbl
            WHERE movement_date BETWEEN :fromDate AND :toDate
            """;

        return databaseClient.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map((row, meta) -> new KpiTotals(
                        row.get("total_inbound", BigDecimal.class),
                        row.get("total_consumed", BigDecimal.class),
                        row.get("total_wastage", BigDecimal.class),
                        row.get("total_stock_value", BigDecimal.class)))
                .one();
    }

    // ─── Daily trend points for charting ───────────────────────────

    public Flux<DailyTrendRow> findDailyTrend(int days) {
        String sql = """
            SELECT
                d::date AS day,
                COALESCE(SUM(CASE WHEN mv.movement_type = 'INBOUND'     THEN mv.quantity ELSE 0 END), 0) AS inbound,
                COALESCE(ABS(SUM(CASE WHEN mv.movement_type = 'CONSUMPTION' THEN mv.quantity ELSE 0 END)), 0) AS consumed,
                COALESCE(ABS(SUM(CASE WHEN mv.movement_type = 'WASTAGE'     THEN mv.quantity ELSE 0 END)), 0) AS wastage
            FROM generate_series(CURRENT_DATE - (:days - 1) * INTERVAL '1 day', CURRENT_DATE, INTERVAL '1 day') d
            LEFT JOIN erp_finance_schema.rm_stock_movement_tbl mv
                   ON mv.movement_date = d::date
            GROUP BY d
            ORDER BY d
            """;

        return databaseClient.sql(sql)
                .bind("days", days)
                .map((row, meta) -> new DailyTrendRow(
                        row.get("day", LocalDate.class),
                        row.get("inbound", BigDecimal.class),
                        row.get("consumed", BigDecimal.class),
                        row.get("wastage", BigDecimal.class)))
                .all();
    }

    // ─── Wastage breakdown by reason for pie chart ────────────────

    public Flux<WastageReasonRow> findWastageBreakdown(int days) {
        String sql = """
            SELECT
                reason_code,
                ABS(SUM(quantity)) AS total_qty,
                COUNT(*)            AS event_count
            FROM erp_finance_schema.rm_stock_movement_tbl
            WHERE movement_type = 'WASTAGE'
              AND movement_date >= CURRENT_DATE - (:days - 1) * INTERVAL '1 day'
            GROUP BY reason_code
            ORDER BY total_qty DESC
            """;

        return databaseClient.sql(sql)
                .bind("days", days)
                .map((row, meta) -> new WastageReasonRow(
                        row.get("reason_code", String.class),
                        row.get("total_qty", BigDecimal.class),
                        row.get("event_count", Long.class)))
                .all();
    }

    // ─── Plain DTOs for query results (kept here for cohesion) ────

    public record MaterialStockRow(
            String materialId, String materialName, String category, String uom,
            BigDecimal reorderLevel, BigDecimal safetyStockLevel,
            BigDecimal stockOnHand, BigDecimal inboundPeriod,
            BigDecimal consumedPeriod, BigDecimal wastagePeriod) {}

    public record KpiTotals(
            BigDecimal totalInbound, BigDecimal totalConsumed,
            BigDecimal totalWastage, BigDecimal totalStockValue) {}

    public record DailyTrendRow(
            LocalDate date, BigDecimal inbound, BigDecimal consumed, BigDecimal wastage) {}

    public record WastageReasonRow(
            String reasonCode, BigDecimal totalQty, Long eventCount) {}
}
