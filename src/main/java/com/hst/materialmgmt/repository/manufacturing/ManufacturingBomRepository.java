package com.hst.materialmgmt.repository.manufacturing;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingBomEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.manufacturing.ManufacturingBomRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ManufacturingBomRepository extends ParentRepositoryImpl {

    @Autowired private ManufacturingBomRowMapper rowMapper;

    @Override protected String getTableName() { return "manufacturing_bom_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("bom_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) ManufacturingBomEntity.class; }

    public Flux<ManufacturingBomEntity> findByProductId(String productId) {
        return databaseClient.sql("""
            SELECT b.*, m.material_name
            FROM rm_material_schema.manufacturing_bom_tbl b
            LEFT JOIN rm_material_schema.rm_material_tbl m ON m.material_id = b.material_id
            WHERE b.product_id = :productId AND b.is_active = TRUE
            """)
                .bind("productId", productId)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Flux<ManufacturingBomEntity> findAllBoms() {
        return databaseClient.sql("""
            SELECT b.*, m.material_name, p.name AS product_name
            FROM rm_material_schema.manufacturing_bom_tbl b
            LEFT JOIN rm_material_schema.rm_material_tbl m ON m.material_id = b.material_id
            LEFT JOIN rm_material_schema.product_tbl p ON p.product_id = b.product_id
            ORDER BY b.product_id, m.material_name
            """)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<String> nextBomId() {
        return databaseClient
                .sql("SELECT nextval('rm_material_schema.manufacturing_bom_seq')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("BOM-%06d", n));
    }
}