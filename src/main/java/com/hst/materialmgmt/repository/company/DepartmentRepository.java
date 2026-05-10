package com.hst.materialmgmt.repository.company;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.company.DepartmentEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;

@Repository
public class DepartmentRepository extends ParentRepositoryImpl {
	private static final String TABLE_NAME = "department_tbl";


	@SuppressWarnings("unchecked")
	@Override
	protected Class<DepartmentEntity> getEntityClass() {
		return DepartmentEntity.class;
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
