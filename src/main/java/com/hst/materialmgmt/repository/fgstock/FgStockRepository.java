package com.hst.materialmgmt.repository.fgstock;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.fgstock.FgStockEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.fgstock.FgStockRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FgStockRepository extends ParentRepositoryImpl {

    @Autowired private FgStockRowMapper rowMapper;

    @Override protected String getTableName() { return "fg_stock_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("fg_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) FgStockEntity.class; }

    public Flux<FgStockEntity> findAllStock() {
        return databaseClient.sql("""
            SELECT f.*, p.name AS product_name, p.sku
            FROM rm_material_schema.fg_stock_tbl f
            LEFT JOIN rm_material_schema.product_tbl p ON p.product_id = f.product_id
            ORDER BY p.name
            """)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<FgStockEntity> findByProductId(String productId) {
        return databaseClient.sql("""
            SELECT * FROM rm_material_schema.fg_stock_tbl
            WHERE product_id = :productId
            """)
                .bind("productId", productId)
                .map((row, meta) -> rowMapper.apply(row, meta)).one();
    }

    public Mono<Void> upsertStock(String productId, int delta) {
        return databaseClient.sql("""
            INSERT INTO rm_material_schema.fg_stock_tbl
                (fg_id, product_id, quantity, unit, created_by, updated_by)
            VALUES (
                'FG-' || LPAD(nextval('rm_material_schema.fg_stock_seq')::text, 6, '0'),
                :productId, :delta, 'PCS', 'system', 'system'
            )
            ON CONFLICT (product_id) DO UPDATE
                SET quantity   = rm_material_schema.fg_stock_tbl.quantity + :delta,
                    updated_at = CURRENT_TIMESTAMP,
                    updated_by = 'system'
            """)
                .bind("productId", productId)
                .bind("delta", delta)
                .fetch().rowsUpdated().then();
    }

    public Mono<String> nextFgId() {
        return databaseClient
                .sql("SELECT nextval('rm_material_schema.fg_stock_seq')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("FG-%06d", n));
    }
}