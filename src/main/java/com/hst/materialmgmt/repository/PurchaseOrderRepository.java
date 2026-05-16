package com.hst.materialmgmt.repository;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.PurchaseOrderEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.PurchaseOrderRowMapper;

@Repository
public class PurchaseOrderRepository extends ParentRepositoryImpl {
    private static final String TABLE_NAME = "rm_purchase_order_tbl";

    @Autowired 
    private PurchaseOrderRowMapper purchaseOrderRowMapper;

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Map<String, Object> getKeyParamMap(String id) {
        return Map.of("po_id", id);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> BaseRowMapper<T> getRowMapper() {
        return (BaseRowMapper<T>) purchaseOrderRowMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Class<T> getEntityClass() {
       return (Class<T>) PurchaseOrderEntity.class;
    }
}
