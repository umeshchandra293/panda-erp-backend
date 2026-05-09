package com.hst.materialmgmt.repository;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.GrnEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.GrnRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class GrnRepository extends ParentRepositoryImpl {

    @Autowired private GrnRowMapper rowMapper;

    @Override protected String getTableName() { return "rm_grn_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("grn_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) GrnEntity.class; }

    public Flux<GrnEntity> findAllGrns() {
        String sql = """
            SELECT g.*, s.supplier_name
            FROM erp_finance_schema.rm_grn_tbl g
            LEFT JOIN erp_finance_schema.rm_supplier_tbl s
                   ON s.supplier_code = g.supplier_code
            ORDER BY g.received_date DESC, g.created_at DESC
            """;
        return databaseClient.sql(sql)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<GrnEntity> findByGrnId(String grnId) {
        String sql = "SELECT * FROM erp_finance_schema.rm_grn_tbl WHERE grn_id = :grnId";
        return databaseClient.sql(sql)
                .bind("grnId", grnId)
                .map((row, meta) -> rowMapper.apply(row, meta)).one();
    }

    public Mono<String> nextGrnId() {
        return databaseClient
                .sql("SELECT nextval('erp_finance_schema.grn_code_seq')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("GRN-%06d", n));
    }
}