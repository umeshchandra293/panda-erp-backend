package com.hst.materialmgmt.rowMapper.company;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.company.CompanyEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class CompanyRowMapper extends BaseRowMapper<CompanyEntity> {

	@Override
	public CompanyEntity apply(Row row, RowMetadata u) {
	    CompanyEntity entity =
			CompanyEntity.builder()
			.companyId(row.get("company_id", String.class))
			.name(row.get("company_name", String.class))
			.legalName(row.get("legal_name", String.class))
			.taxId(row.get("tax_id", String.class))
			.emailId(row.get("email_id", String.class))
			.phone(row.get("phone", String.class))
			.website(row.get("website", String.class))
			.address(row.get("address", String.class))
			.build();
		populateAuditInfo(entity, row);
		return entity;
	}
}
