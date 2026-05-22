package com.hst.materialmgmt.repository;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.GrnItemEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.GrnItemRowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class GrnItemRepository extends ParentRepositoryImpl {

    @Autowired private GrnItemRowMapper rowMapper;

    @Override protected String getTableName() { return "rm_grn_item_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("grn_item_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) GrnItemEntity.class; }

    public Flux<GrnItemEntity> findByGrnId(String grnId) {
        String sql = """
            SELECT gi.*
            FROM rm_material_schema.rm_grn_item_tbl gi
            WHERE gi.grn_id = :grnId
            """;
        return databaseClient.sql(sql)
                .bind("grnId", grnId)
                .map((row, meta) -> rowMapper.apply(row, meta)).all();
    }

    public Mono<String> nextGrnItemId() {
        return databaseClient
                .sql("SELECT nextval('rm_material_schema.grn_item_code_seq')")
                .map((row, meta) -> row.get(0, Long.class)).one()
                .map(n -> String.format("GRNI-%06d", n));
    }
}