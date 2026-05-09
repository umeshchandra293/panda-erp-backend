package com.hst.materialmgmt.repository;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.PoItemEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.PoItemRowMapper;

@Repository
public class PoItemRepository extends ParentRepositoryImpl {
    
    @Autowired 
    private PoItemRowMapper poItemRowMapper;

    @Override 
    protected String getTableName() { 
        return "erp_finance_schema.rm_po_item_tbl"; 
    }
    
    @Override 
    protected Map<String, Object> getKeyParamMap(String id) { 
        return Map.of("item_id", Long.valueOf(id)); 
    }
    
    @SuppressWarnings("unchecked")
    @Override 
    protected <T> BaseRowMapper<T> getRowMapper() { 
        return (BaseRowMapper<T>) poItemRowMapper; 
    }
    
    @SuppressWarnings("unchecked")
    @Override 
    protected <T> Class<T> getEntityClass() { 
        return (Class<T>) PoItemEntity.class; 
    }
}