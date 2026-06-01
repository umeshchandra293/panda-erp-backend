package com.hst.materialmgmt.repository;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.hst.materialmgmt.entity.RawMaterialEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.RawMaterialRowMapper;
import reactor.core.publisher.Mono;

@Repository
public class RawMaterialRepository extends ParentRepositoryImpl {

    @Autowired private RawMaterialRowMapper rowMapper;

    @Override protected String getTableName() { return "rm_material_tbl"; }
    @Override protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("material_id", id);
    }
    @SuppressWarnings("unchecked")
    @Override protected <T> BaseRowMapper<T> getRowMapper() { return (BaseRowMapper<T>) rowMapper; }
    @SuppressWarnings("unchecked")
    @Override protected <T> Class<T> getEntityClass() { return (Class<T>) RawMaterialEntity.class; }

    // Direct SQL update for unit_price — bypasses generic buildDataParams
    public Mono<Void> updateUnitPrice(String materialId, BigDecimal unitPrice) {
        return databaseClient
                .sql("UPDATE rm_material_schema.rm_material_tbl SET unit_price = :unitPrice WHERE material_id = :materialId")
                .bind("unitPrice", unitPrice)
                .bind("materialId", materialId)
                .fetch()
                .rowsUpdated()
                .then();
    }
}