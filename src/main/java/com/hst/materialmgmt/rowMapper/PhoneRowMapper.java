package com.hst.materialmgmt.rowMapper;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.PhoneEntity;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class PhoneRowMapper extends BaseRowMapper<PhoneEntity> {
	
	@Override
	public PhoneEntity apply(Row row, RowMetadata rowMetadata) {
		PhoneEntity entity =
			PhoneEntity.builder()
            	.phoneId(row.get("phone_id", String.class))
            	.phoneType(row.get("phone_type", String.class))
            	.phoneNumber(row.get("phone_number", String.class))
            	.phoneExtension(row.get("phone_extension", String.class))
            	.build();
		populateAuditInfo(entity, row);
		return entity;
	}
}
