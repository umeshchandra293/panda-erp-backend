package com.hst.materialmgmt.production.repository;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.production.entity.ProductionBatchEntity;
import com.hst.materialmgmt.production.rowMapper.ProductionBatchRowMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductionBatchRepository extends ParentRepositoryImpl {

    @Autowired private ProductionBatchRowMapper rowMapper;

    @Override protected String getTableName() { return "production_batch_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("batch_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() {
        return (Class<T>) ProductionBatchEntity.class;
    }

    public Flux<ProductionBatchEntity> findByShiftId(String shiftId) {
        String sql = """
            SELECT b.*, p.product_name
            FROM erp_finance_schema.production_batch_tbl b
            LEFT JOIN erp_finance_schema.sales_product_tbl p
                   ON p.product_id = b.product_id
            WHERE b.shift_id = :shiftId
            """;
        return databaseClient.sql(sql)
                .bind("shiftId", shiftId)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<String> nextBatchId() {
        return databaseClient
                .sql("SELECT nextval('erp_finance_schema.batch_seq')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("BAT-%06d", n));
    }
}