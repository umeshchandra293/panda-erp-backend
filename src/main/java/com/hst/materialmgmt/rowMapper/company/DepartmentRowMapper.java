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
        DepartmentEntity entity = DepartmentEntity.builder()
                .departmentId(row.get("department_id",  String.class))
                .companyId(row.get("company_id",        String.class))
                .name(row.get("name",                   String.class))  // ← was "department_name"
                .code(row.get("code",                   String.class))  // ← was "department_code"
                .parentDeptId(row.get("parent_dept_id", String.class))  // ← was "parent_department_id"
                .build();
        populateAuditInfo(entity, row);
        return entity;
    }
}
