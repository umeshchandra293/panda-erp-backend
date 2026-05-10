package com.hst.materialmgmt.objectMapper.company;

import org.springframework.stereotype.Component;

import com.hst.api.model.Employee;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.company.EmployeeEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;

@Component
public class EmployeeMapper extends BaseMapper {

	@Override
	public BaseEntity toEntity(Object modelObject, Object entityObject, boolean isNew) {
		Employee employee = (Employee) modelObject;

	    EmployeeEntity updateEntity = null;

	    if (isNew || entityObject == null) {
	      updateEntity = new EmployeeEntity();
	    } else {
	      updateEntity = (EmployeeEntity) entityObject;
	    }

		updateEntity.setEmployeeId(employee.getEmployeeId());
		updateEntity.setCompanyId(employee.getCompanyId());
		updateEntity.setDepartmentId(employee.getDepartmentId());
		updateEntity.setEmployeeCode(employee.getEmployeeCode());
		updateEntity.setFirstName(employee.getFirstName());
		updateEntity.setLastName(employee.getLastName());
		updateEntity.setEmail(employee.getEmail());
		updateEntity.setPhone(employee.getPhone());
		updateEntity.setJobTitle(employee.getJobTitle());
		updateEntity.setManagerId(employee.getManagerId());
		updateEntity.setHireDate(employee.getHireDate());
		updateEntity.setTerminationDate(employee.getTerminationDate());
		updateEntity.setStatus(employee.getStatus());
	    return updateEntity;
	}

	@Override
	public Object toModel(Object entityObject) {
		if (entityObject == null) {
	      return null;
	    }

	    EmployeeEntity employeeEntity = (EmployeeEntity) entityObject;
	    Employee employee = new Employee();
	    employee.setEmployeeId(employeeEntity.getEmployeeId());
	    employee.setCompanyId(employeeEntity.getCompanyId());
	    employee.setDepartmentId(employeeEntity.getDepartmentId());
	    employee.setEmployeeCode(employeeEntity.getEmployeeCode());
	    employee.setFirstName(employeeEntity.getFirstName());
	    employee.setLastName(employeeEntity.getLastName());
	    employee.setEmail(employeeEntity.getEmail());
	    employee.setPhone(employeeEntity.getPhone());
	    employee.setJobTitle(employeeEntity.getJobTitle());
	    employee.setManagerId(employeeEntity.getManagerId());
	    employee.setHireDate(employeeEntity.getHireDate());
	    employee.setTerminationDate(employeeEntity.getTerminationDate());
	    employee.setStatus(employeeEntity.getStatus());
	    return employee;
	}
}