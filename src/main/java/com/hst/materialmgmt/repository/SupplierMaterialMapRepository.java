package com.hst.materialmgmt.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.SupplierMaterialMapEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.SupplierMaterialMapRowMapper;

import reactor.core.publisher.Flux;

@Repository
public class SupplierMaterialMapRepository extends ParentRepositoryImpl {

    private static final String TABLE_NAME = "rm_supplier_material_map_tbl";

    @Autowired
    private SupplierMaterialMapRowMapper rowMapper;

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("mapping_id", id);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> BaseRowMapper<T> getRowMapper() {
        return (BaseRowMapper<T>) rowMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Class<T> getEntityClass() {
        return (Class<T>) SupplierMaterialMapEntity.class;
    }

    /**
     * Fetch all active mappings for a given supplier.
     * This is the query that powers the PO form's material dropdown.
     */
    public Flux<SupplierMaterialMapEntity> findBySupplierCode(String supplierCode) {
        String sql = "SELECT * FROM " + getTableNameWithQualifier()
                + " WHERE supplier_code = :supplierCode AND is_active = TRUE"
                + " ORDER BY material_id";

        return databaseClient.sql(sql)
                .bind("supplierCode", supplierCode)
                .map((row, meta) -> rowMapper.apply(row, meta))
                .all();
    }

    /**
     * Fetch a specific mapping by supplier + material (the natural key).
     */
    public reactor.core.publisher.Mono<SupplierMaterialMapEntity> findBySupplierAndMaterial(
            String supplierCode, String materialId) {
        String sql = "SELECT * FROM " + getTableNameWithQualifier()
                + " WHERE supplier_code = :supplierCode AND material_id = :materialId";

        return databaseClient.sql(sql)
                .bind("supplierCode", supplierCode)
                .bind("materialId", materialId)
                .map((row, meta) -> rowMapper.apply(row, meta))
                .one();
    }
}