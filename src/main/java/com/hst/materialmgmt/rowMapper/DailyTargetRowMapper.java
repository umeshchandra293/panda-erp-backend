package com.hst.materialmgmt.rowMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.DailyTargetEntity;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class DailyTargetRowMapper extends BaseRowMapper<DailyTargetEntity> {
    @Override
    public DailyTargetEntity apply(Row row, RowMetadata meta) {
        DailyTargetEntity e = DailyTargetEntity.builder()
                .targetId(row.get("target_id",         String.class))
                .salesmanId(row.get("salesman_id",     String.class))
                .targetDate(row.get("target_date",     LocalDate.class))
                .visitTarget(row.get("visit_target",   Integer.class))
                .orderTarget(row.get("order_target",   Integer.class))
                .collectionTarget(row.get("collection_target", BigDecimal.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}