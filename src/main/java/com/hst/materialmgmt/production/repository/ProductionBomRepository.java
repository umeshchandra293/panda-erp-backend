package com.hst.materialmgmt.production.repository;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.production.entity.ProductionBomEntity;
import com.hst.materialmgmt.production.rowMapper.ProductionBomRowMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import reactor.core.publisher.Flux;

@Repository
public class ProductionBomRepository extends ParentRepositoryImpl {

    @Autowired private ProductionBomRowMapper rowMapper;

    @Override protected String getTableName() { return "production_bom_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("bom_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() {
        return (Class<T>) ProductionBomEntity.class;
    }

    public Flux<ProductionBomEntity> findByProductId(String productId) {
        String sql = """
            SELECT b.*, m.material_name
            FROM erp_finance_schema.production_bom_tbl b
            LEFT JOIN erp_finance_schema.rm_material_tbl m
                   ON m.material_id = b.material_id
            WHERE b.product_id = :productId
            """;
        return databaseClient.sql(sql)
                .bind("productId", productId)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }
}
