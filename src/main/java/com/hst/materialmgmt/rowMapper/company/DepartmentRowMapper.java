package com.hst.materialmgmt.rowMapper.company;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.company.DepartmentEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class DepartmentRowMapper extends BaseRowMapper<DepartmentEntity> {

	@Override
	public DepartmentEntity apply(Row row, RowMetadata u) {
	    DepartmentEntity entity =
    		DepartmentEntity.builder()
            .departmentId(row.get("department_id", String.class))
            .companyId(row.get("company_id", String.class))
            .name(row.get("department_name", String.class))
            .code(row.get("department_code", String.class))
            .parentDeptId(row.get("parent_department_id", String.class))
            .build();
	    populateAuditInfo(entity, row);
	    return entity;
	}
}
