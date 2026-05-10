package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.company.EmployeeMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.company.EmployeeRepository;

@Service
public class EmployeeService extends SingleEntityBaseService {
	
	@Autowired 
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private EmployeeMapper employeeMapper;

	@Override
	protected BaseMapper getMapper() {
		return employeeMapper;
	}

	@Override
	protected ParentRepositoryImpl getMasterRepository() {
		return employeeRepository;
	}
}
