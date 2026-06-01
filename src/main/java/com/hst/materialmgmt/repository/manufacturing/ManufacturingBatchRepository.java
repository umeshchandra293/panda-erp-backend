package com.hst.materialmgmt.repository.manufacturing;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingBatchEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.manufacturing.ManufacturingBatchRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ManufacturingBatchRepository extends ParentRepositoryImpl {

    @Autowired private ManufacturingBatchRowMapper rowMapper;

    @Override protected String getTableName() { return "manufacturing_batch_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) { return Map.of("batch_id", id); }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) ManufacturingBatchEntity.class; }

    public Flux<ManufacturingBatchEntity> findByShiftId(String shiftId) {
        return databaseClient.sql("""
            SELECT b.*, p.name AS product_name, p.sku
            FROM rm_material_schema.manufacturing_batch_tbl b
            LEFT JOIN rm_material_schema.product_tbl p ON p.product_id = b.product_id
            WHERE b.shift_id = :shiftId
            """)
                .bind("shiftId", shiftId)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<String> nextBatchId() {
        return databaseClient.sql("""
            SELECT s.n FROM generate_series(1,(SELECT COUNT(*)+1 FROM rm_material_schema.manufacturing_batch_tbl)) AS s(n)
            WHERE NOT EXISTS (
                SELECT 1 FROM rm_material_schema.manufacturing_batch_tbl
                WHERE batch_id = CONCAT('BATCH-', LPAD(s.n::text,6,'0'))
            ) ORDER BY s.n LIMIT 1
            """)
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("BATCH-%06d", n));
    }
}