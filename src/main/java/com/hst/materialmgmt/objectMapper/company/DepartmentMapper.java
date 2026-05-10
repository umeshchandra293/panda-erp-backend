package com.hst.materialmgmt.objectMapper.company;

import org.springframework.stereotype.Component;

import com.hst.api.model.Department;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.company.DepartmentEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;

@Component
public class DepartmentMapper extends BaseMapper {

	@Override
	public BaseEntity toEntity(Object modelObject, Object entityObject, boolean isNew) {
		Department department = (Department) modelObject;

	    DepartmentEntity updateEntity = null;

	    if (isNew || entityObject == null) {
	      updateEntity = new DepartmentEntity();
	    } else {
	      updateEntity = (DepartmentEntity) entityObject;
	    }

		updateEntity.setDepartmentId(department.getDepartmentId());
		updateEntity.setCompanyId(department.getCompanyId());
		updateEntity.setName(department.getName());
		updateEntity.setCode(department.getCode());
		updateEntity.setParentDeptId(department.getParentDeptId());

	    return updateEntity;
	}

	@Override
	public Object toModel(Object entityObject) {
		if (entityObject == null) {
	      return null;
	    }
	    DepartmentEntity departmentEntity = (DepartmentEntity) entityObject;
	    Department department = new Department();
	    department.setDepartmentId(departmentEntity.getDepartmentId());
	    department.setCompanyId(departmentEntity.getCompanyId());
	    department.setName(departmentEntity.getName());
	    department.setCode(departmentEntity.getCode());
	    department.setParentDeptId(departmentEntity.getParentDeptId());
	    return department;
	}
}
