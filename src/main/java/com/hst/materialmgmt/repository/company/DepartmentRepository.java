package com.hst.materialmgmt.repository.company;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.company.DepartmentEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.company.DepartmentRowMapper;

@Repository
public class DepartmentRepository extends ParentRepositoryImpl {

	private static final String TABLE_NAME = "department_tbl";
	private static final String TABLE_NAME_KEY = "department_id";
	
	@Autowired 
  	private DepartmentRowMapper departmentRowMapper;

	@SuppressWarnings("unchecked")
	@Override
	protected Class<DepartmentEntity> getEntityClass() {
		return DepartmentEntity.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected BaseRowMapper<DepartmentEntity> getRowMapper() {
		return departmentRowMapper;
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
