package com.hst.materialmgmt.rowMapper;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.RetailerEntity;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class RetailerRowMapper extends BaseRowMapper<RetailerEntity> {
    @Override
    public RetailerEntity apply(Row row, RowMetadata meta) {
        RetailerEntity e = RetailerEntity.builder()
                .retailerId(row.get("retailer_id", String.class))
                .shopName(row.get("shop_name", String.class))
                .ownerName(row.get("owner_name", String.class))
                .phone(row.get("phone", String.class))
                .address(row.get("address", String.class))
                .area(row.get("area", String.class))
                .gpsLat(row.get("gps_lat", BigDecimal.class))
                .gpsLng(row.get("gps_lng", BigDecimal.class))
                .assignedSalesmanId(row.get("assigned_salesman_id", String.class))
                .creditLimit(row.get("credit_limit", BigDecimal.class))
                .currentBalance(row.get("current_balance", BigDecimal.class))
                .isActive(row.get("is_active", Boolean.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}