package com.hst.materialmgmt.rowMapper.fgstock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.fgstock.FgDispatchEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class FgDispatchRowMapper extends BaseRowMapper<FgDispatchEntity> {
    @Override
    public FgDispatchEntity apply(Row row, RowMetadata meta) {
        FgDispatchEntity e = FgDispatchEntity.builder()
            .dispatchId(row.get("dispatch_id",       String.class))
            .dispatchDate(row.get("dispatch_date",   LocalDate.class))
            .driverName(row.get("driver_name",       String.class))
            .driverPhone(row.get("driver_phone",     String.class))
            .vehicleNumber(row.get("vehicle_number", String.class))
            .deliveryOrder(row.get("delivery_order", String.class))
            .destination(row.get("destination",      String.class))
            .status(row.get("status",                String.class))
            .amountToCollect(row.get("amount_to_collect",  BigDecimal.class))
            .amountCollected(row.get("amount_collected",   BigDecimal.class))
            .paymentMode(row.get("payment_mode",     String.class))
            .notes(row.get("notes",                  String.class))
            .settledAt(row.get("settled_at",         LocalDateTime.class))
            .build();
        populateAuditInfo(e, row);
        return e;
    }
}