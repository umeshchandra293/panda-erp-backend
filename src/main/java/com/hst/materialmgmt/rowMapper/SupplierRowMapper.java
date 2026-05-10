package com.hst.materialmgmt.rowMapper;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.supplier.SupplierEntity;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class SupplierRowMapper extends BaseRowMapper<SupplierEntity>{
	@Override
	public SupplierEntity apply(Row row, RowMetadata rowMetadata) {
	    SupplierEntity entity =
    		SupplierEntity.builder()
            .supplierCode(row.get("vendor_code", String.class))
            .supplierName(row.get("vendor_name", String.class))
            .supplierCategory(row.get("vendor_category", String.class))
            .supplierGroup(row.get("vendor_group", String.class))
            .legalEntity(row.get("legal_entity_id", String.class))
            .gstNumber(row.get("gst_number", String.class))
            .panNumber(row.get("pan_number", String.class))
            .effectiveDate(row.get("effective_date", java.time.LocalDate.class))
            .endDate(row.get("end_date", java.time.LocalDate.class))
            .paymentTerm(row.get("payment_term", String.class))
            .build();
	    populateAuditInfo(entity, row);
	    return entity;
	}
}
