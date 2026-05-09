package com.hst.materialmgmt.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.RawMaterialEntity; // Ensure this is the correct path
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.RawMaterialRowMapper;

@Repository
public class RawMaterialRepository extends ParentRepositoryImpl {

    @Autowired
    private RawMaterialRowMapper rawMaterialRowMapper;

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Class<T> getEntityClass() {
        return (Class<T>) RawMaterialEntity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> BaseRowMapper<T> getRowMapper() {
        return (BaseRowMapper<T>) rawMaterialRowMapper;
    }

    @Override
    protected String getTableName() {
        // IMPORTANT: Verify this exact table name in your PostgreSQL database!
        return "rm_material_tbl"; 
    }

    @Override
    protected Map<String, Object> getKeyParamMap(String id) {
        // IMPORTANT: Verify this exact primary key column name in your database!
        return Map.of("material_id", id);
    }
}