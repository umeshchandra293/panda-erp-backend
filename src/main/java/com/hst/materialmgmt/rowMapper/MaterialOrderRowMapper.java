package com.hst.materialmgmt.rowMapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.MaterialOrderEntity;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class MaterialOrderRowMapper extends BaseRowMapper<MaterialOrderEntity>{
	
	@Override
	public MaterialOrderEntity apply(Row row, RowMetadata rowMetadata) {
		MaterialOrderEntity entity =
			MaterialOrderEntity.builder()
			.orderId(row.get("order_id", String.class))
            .supplierCode(row.get("supplier_code", String.class))
            .orderNumber(row.get("order_number", String.class))
            //.orderDate(row.get("order_date", String.class))
            .exptdDeliDate(row.get("exptd_deli_date", LocalDate.class))
            .actualDeliDate(row.get("actual_deli_date", LocalDate.class))
            .status(row.get("status", String.class))
            .totalAmount(row.get("total_amount", Double.class))
            .ordCreatedBy(row.get("ord_created_by", String.class))
            .approvedBy(row.get("approved_by", String.class))
            .notes(row.get("notes", String.class))
            .build();
	    populateAuditInfo(entity, row);
	    return entity;
	}


}
