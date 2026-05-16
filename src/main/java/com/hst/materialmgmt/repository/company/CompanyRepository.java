package com.hst.materialmgmt.repository.company;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.company.CompanyEntity;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.company.CompanyRowMapper;

@Repository
public class CompanyRepository extends ParentRepositoryImpl {
	
	private static final String TABLE_NAME = "company_tbl";
	private static final String TABLE_NAME_KEY = "company_id";

  	@Autowired 
  	private CompanyRowMapper companyRowMapper;


	@SuppressWarnings("unchecked")
	@Override
	protected Class<CompanyEntity> getEntityClass() {
		return CompanyEntity.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected BaseRowMapper<CompanyEntity> getRowMapper() {
		return companyRowMapper;
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
