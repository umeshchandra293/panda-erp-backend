package com.hst.materialmgmt.repository.fgstock;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.fgstock.FgDispatchItemEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.fgstock.FgDispatchItemRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FgDispatchItemRepository extends ParentRepositoryImpl {

    @Autowired private FgDispatchItemRowMapper rowMapper;

    @Override protected String getTableName() { return "fg_dispatch_item_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("dispatch_item_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) FgDispatchItemEntity.class; }

    public Flux<FgDispatchItemEntity> findByDispatchId(String dispatchId) {
        return databaseClient.sql("""
            SELECT i.*, p.name AS product_name
            FROM rm_material_schema.fg_dispatch_item_tbl i
            LEFT JOIN rm_material_schema.product_tbl p ON p.product_id = i.product_id
            WHERE i.dispatch_id = :dispatchId
            """)
            .bind("dispatchId", dispatchId)
            .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<FgDispatchItemEntity> findByItemId(String id) {
        return databaseClient.sql(
            "SELECT * FROM rm_material_schema.fg_dispatch_item_tbl WHERE dispatch_item_id = :id")
            .bind("id", id)
            .map((row, meta) -> rowMapper.apply(row, meta)).one();
    }

    public Mono<Void> updateReturns(String dispatchItemId, int casesReturned, int bottlesReturned) {
        return databaseClient.sql("""
            UPDATE rm_material_schema.fg_dispatch_item_tbl
            SET cases_returned = :casesReturned,
                bottles_returned = :bottlesReturned,
                updated_at = NOW()
            WHERE dispatch_item_id = :id
            """)
            .bind("id",              dispatchItemId)
            .bind("casesReturned",   casesReturned)
            .bind("bottlesReturned", bottlesReturned)
            .fetch().rowsUpdated().then();
    }

    public Mono<String> nextItemId() {
        return databaseClient.sql("""
            SELECT s.n FROM generate_series(1,(SELECT COUNT(*)+1 FROM rm_material_schema.fg_dispatch_item_tbl)) AS s(n)
            WHERE NOT EXISTS (
                SELECT 1 FROM rm_material_schema.fg_dispatch_item_tbl
                WHERE dispatch_item_id = CONCAT('DSPI-', LPAD(s.n::text,6,'0'))
            ) ORDER BY s.n LIMIT 1
            """)
            .map((row, meta) -> row.get(0, Long.class)).one()
            .map(n -> String.format("DSPI-%06d", n));
    }
}