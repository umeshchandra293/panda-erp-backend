package com.hst.materialmgmt.repository.company;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.company.EmployeeEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.company.EmployeeRowMapper;

@Repository
public class EmployeeRepository extends ParentRepositoryImpl {
	private static final String TABLE_NAME = "employee_tbl";
	private static final String TABLE_NAME_KEY = "employee_id";
	
	@Autowired 
  	private EmployeeRowMapper employeeRowMapper;

	@SuppressWarnings("unchecked")
	@Override
	protected Class<EmployeeEntity> getEntityClass() {
		return EmployeeEntity.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected BaseRowMapper<EmployeeEntity> getRowMapper() {
		return employeeRowMapper;
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected Map<String, Object> getKeyParamMap(String id) {
		Map<String, Object> keyParams = Map.of(TABLE_NAME_KEY, id);
		return keyParams;
	}
}
