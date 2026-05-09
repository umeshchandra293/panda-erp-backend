package com.hst.materialmgmt.rowMapper;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.AddressEntity;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class AddressRowMapper extends BaseRowMapper<AddressEntity> {

    @Override
    public AddressEntity apply(Row row, RowMetadata rowMetadata) {
        AddressEntity addressEntity = AddressEntity.builder()
                .addressId(row.get("address_id", String.class))
                .addressType(row.get("address_type", String.class))
                .addressLine1(row.get("address_line_1", String.class))
                .addressLine2(row.get("address_line_2", String.class))
                .poBoxNumber(row.get("po_box_number", String.class))
                .city(row.get("city", String.class))
                .stateCode(row.get("state_cd", String.class))
                .postalCode(row.get("postal_cd", String.class))
                .countryCode(row.get("country_cd", String.class))
                .timeZone(row.get("time_zone", String.class))
                .isPrimary(row.get("is_primary", Boolean.class))
                .build();

        populateAuditInfo(addressEntity, row);
        return addressEntity;
    }
}