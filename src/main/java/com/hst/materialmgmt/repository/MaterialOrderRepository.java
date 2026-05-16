package com.hst.materialmgmt.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.supplier.SupplierEntity;
import com.hst.materialmgmt.rowMapper.SupplierRowMapper;

@Repository
public class MaterialOrderRepository extends ParentRepositoryImpl {

	private static final String TABLE_NAME = "rm_material_order_tbl";

	@Autowired 
	private SupplierRowMapper supplierRowMapper;

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected Map<String, Object> getKeyParamMap(String id) {
		Map<String, Object> keyParams = Map.of("supplier_code", id);
		return keyParams;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierRowMapper getRowMapper() {
	    return supplierRowMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<SupplierEntity> getEntityClass() {
	   return SupplierEntity.class;
	}

}
