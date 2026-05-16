package com.hst.materialmgmt.rowMapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.supplier.SupplierEntity;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class SupplierRowMapper extends BaseRowMapper<SupplierEntity> {

    @Override
    public SupplierEntity apply(Row row, RowMetadata rowMetadata) {
        SupplierEntity entity = SupplierEntity.builder()
                .supplierCode(row.get("supplier_code", String.class))
                .supplierName(row.get("supplier_name", String.class))
                .supplierCategory(row.get("supplier_category", String.class))
                .supplierGroup(row.get("supplier_group", String.class))
                .legalEntity(row.get("legal_entity_id", String.class))
                .contactPersonName(row.get("contact_person_name", String.class))
                .gstNumber(row.get("gst_number", String.class))
                .gstRegistrationType(row.get("gst_registration_type", String.class))
                .panNumber(row.get("pan_number", String.class))
                .stateCode(row.get("state_code", String.class))
                .countryCode(row.get("country_code", String.class))
                .leadTimeDays(row.get("lead_time_days", Integer.class))
                .paymentTerm(row.get("payment_term", String.class))
                .effectiveDate(row.get("effective_date", LocalDate.class))
                .endDate(row.get("end_date", LocalDate.class))
                .isActive(row.get("is_active", Boolean.class))
                .build();

        populateAuditInfo(entity, row);
        return entity;
    }
}
