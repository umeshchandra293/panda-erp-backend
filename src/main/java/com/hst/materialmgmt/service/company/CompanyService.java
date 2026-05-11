package com.hst.materialmgmt.service.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.company.CompanyMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.company.CompanyRepository;
import com.hst.materialmgmt.service.SingleEntityBaseService;

@Service
public class CompanyService extends SingleEntityBaseService {
	
	@Autowired 
	private CompanyRepository companyRepository;
	
	@Autowired
	private CompanyMapper companyMapper;

	@Override
	protected BaseMapper getMapper() {
		return companyMapper;
	}

	@Override
	protected ParentRepositoryImpl getMasterRepository() {
		return companyRepository;
	}
}
