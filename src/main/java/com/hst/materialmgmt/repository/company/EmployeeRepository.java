package com.hst.materialmgmt.repository.company;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.company.EmployeeEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;

@Repository
public class EmployeeRepository extends ParentRepositoryImpl {
	private static final String TABLE_NAME = "employee_tbl";


	@SuppressWarnings("unchecked")
	@Override
	protected Class<EmployeeEntity> getEntityClass() {
		return EmployeeEntity.class;
	}

	@Override
	protected <T> BaseRowMapper<T> getRowMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected Map<String, Object> getKeyParamMap(String id) {
		Map<String, Object> keyParams = Map.of("supplier_code", id);
		return keyParams;
	}
}
