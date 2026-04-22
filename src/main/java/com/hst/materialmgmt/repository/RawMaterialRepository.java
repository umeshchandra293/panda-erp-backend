package com.hst.materialmgmt.repository;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.rowMapper.BaseRowMapper;

@Repository
public class RawMaterialRepository extends ParentRepositoryImpl {

	@Override
	protected <T> Class<T> getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected <T> BaseRowMapper<T> getRowMapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> getKeyParamMap(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
