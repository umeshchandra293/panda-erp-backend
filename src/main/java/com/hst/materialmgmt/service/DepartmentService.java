package com.hst.materialmgmt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hst.materialmgmt.objectMapper.BaseMapper;
import com.hst.materialmgmt.objectMapper.company.DepartmentMapper;
import com.hst.materialmgmt.repository.ParentRepositoryImpl;
import com.hst.materialmgmt.repository.company.DepartmentRepository;

@Service
public class DepartmentService extends SingleEntityBaseService {
	
	@Autowired 
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private DepartmentMapper departmentMapper;

	@Override
	protected BaseMapper getMapper() {
		return departmentMapper;
	}

	@Override
	protected ParentRepositoryImpl getMasterRepository() {
		return departmentRepository;
	}
}
