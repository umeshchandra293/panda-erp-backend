package com.hst.materialmgmt.production.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.production.entity.FgStockEntity;
import com.hst.materialmgmt.production.entity.FgStockMovementEntity;
import com.hst.materialmgmt.production.rowMapper.FgStockMovementRowMapper;
import com.hst.materialmgmt.production.rowMapper.FgStockRowMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FgStockRepository extends ParentRepositoryImpl {

    @Autowired private FgStockRowMapper rowMapper;
    @Autowired private FgStockMovementRowMapper movementRowMapper;

    @Override protected String getTableName() { return "fg_stock_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("fg_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() {
        return (Class<T>) FgStockEntity.class;
    }

    public Mono<FgStockEntity> findByProductId(String productId) {
        String sql = """
            SELECT f.*, p.product_name
            FROM erp_finance_schema.fg_stock_tbl f
            LEFT JOIN erp_finance_schema.sales_product_tbl p
                   ON p.product_id = f.product_id
            WHERE f.product_id = :productId
            """;
        return databaseClient.sql(sql)
                .bind("productId", productId)
                .map((row, meta) -> rowMapper.apply(row, meta)).one();
    }

    public Flux<FgStockEntity> findAllFgStock() {
        String sql = """
            SELECT f.*, p.product_name, p.sku, p.unit
            FROM erp_finance_schema.fg_stock_tbl f
            LEFT JOIN erp_finance_schema.sales_product_tbl p
                   ON p.product_id = f.product_id
            ORDER BY p.product_name
            """;
        return databaseClient.sql(sql)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<Void> addQuantity(String productId, BigDecimal qty) {
        String sql = """
            UPDATE erp_finance_schema.fg_stock_tbl
            SET quantity     = quantity + :qty,
                last_updated = CURRENT_TIMESTAMP
            WHERE product_id = :productId
            """;
        return databaseClient.sql(sql)
                .bind("qty", qty)
                .bind("productId", productId)
                .fetch().rowsUpdated().then();
    }

    public Mono<Void> subtractQuantity(String productId, BigDecimal qty) {
        String sql = """
            UPDATE erp_finance_schema.fg_stock_tbl
            SET quantity     = GREATEST(0, quantity - :qty),
                last_updated = CURRENT_TIMESTAMP
            WHERE product_id = :productId
            """;
        return databaseClient.sql(sql)
                .bind("qty", qty)
                .bind("productId", productId)
                .fetch().rowsUpdated().then();
    }

    public Mono<String> nextFgMovementId() {
        return databaseClient
                .sql("SELECT nextval('erp_finance_schema.fg_movement_seq')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("FGM-%06d", n));
    }

    public Mono<Void> insertFgMovement(
            String movementId, String productId, BigDecimal qty,
            String referenceId, LocalDate date) {
        String sql = """
            INSERT INTO erp_finance_schema.fg_stock_movement_tbl
                (movement_id, product_id, movement_type, quantity,
                 reference_type, reference_id, movement_date, notes,
                 created_at, updated_at, created_by, updated_by)
            VALUES
                (:movId, :productId, 'PRODUCED', :qty,
                 'SHIFT', :refId, :date, 'Production shift entry',
                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'System', 'System')
            """;
        return databaseClient.sql(sql)
                .bind("movId",     movementId)
                .bind("productId", productId)
                .bind("qty",       qty)
                .bind("refId",     referenceId)
                .bind("date",      date)
                .fetch().rowsUpdated().then();
    }

    public Mono<Void> insertDispatchMovement(
            String movementId, String productId, BigDecimal qty,
            String referenceId, String notes, LocalDate date) {
        String sql = """
            INSERT INTO erp_finance_schema.fg_stock_movement_tbl
                (movement_id, product_id, movement_type, quantity,
                 reference_type, reference_id, movement_date, notes,
                 created_at, updated_at, created_by, updated_by)
            VALUES
                (:movId, :productId, 'DISPATCHED', :qty,
                 'MANUAL', :refId, :date, :notes,
                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Admin', 'Admin')
            """;
        return databaseClient.sql(sql)
                .bind("movId",     movementId)
                .bind("productId", productId)
                .bind("qty",       qty)
                .bind("refId",     referenceId != null ? referenceId : "MANUAL")
                .bind("date",      date)
                .bind("notes",     notes != null ? notes : "Manual dispatch")
                .fetch().rowsUpdated().then();
    }

    public Flux<FgStockMovementEntity> findLedger(
            String productId, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("""
            SELECT m.*, p.product_name
            FROM erp_finance_schema.fg_stock_movement_tbl m
            LEFT JOIN erp_finance_schema.sales_product_tbl p
                   ON p.product_id = m.product_id
            WHERE 1=1
            """);
        if (productId != null) sql.append(" AND m.product_id = :productId");
        if (from != null)      sql.append(" AND m.movement_date >= :fromDate");
        if (to != null)        sql.append(" AND m.movement_date <= :toDate");
        sql.append(" ORDER BY m.movement_date DESC, m.created_at DESC");

        var spec = databaseClient.sql(sql.toString());
        if (productId != null) spec = spec.bind("productId", productId);
        if (from != null)      spec = spec.bind("fromDate", from);
        if (to != null)        spec = spec.bind("toDate", to);

        return spec.map((row, meta) -> movementRowMapper.apply(row, meta)).all();
    }
}
