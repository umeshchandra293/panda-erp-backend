package com.hst.materialmgmt.rowMapper.company;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.company.EmployeeEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class EmployeeRowMapper extends BaseRowMapper<EmployeeEntity> {

	@Override
	public EmployeeEntity apply(Row row, RowMetadata u) {
	    EmployeeEntity entity =
    		EmployeeEntity.builder()
            .employeeId(row.get("employee_id", String.class))
            .companyId(row.get("company_id", String.class))
            .departmentId(row.get("department_id", String.class))
            .employeeCode(row.get("employee_code", String.class))
            .firstName(row.get("first_name", String.class))
            .lastName(row.get("last_name", String.class))
            .email(row.get("email", String.class))
            .phone(row.get("phone", String.class))
            .jobTitle(row.get("job_title", String.class))
            .managerId(row.get("manager_id", String.class))
            .hireDate(row.get("hire_date", java.time.LocalDate.class))
            .terminationDate(row.get("termination_date", java.time.LocalDate.class))
            .status(row.get("status", String.class))
            .build();
	    populateAuditInfo(entity, row);
	    return entity;
	}
}
