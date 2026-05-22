package com.hst.materialmgmt.rowMapper.manufacturing;

import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingBatchEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ManufacturingBatchRowMapper extends BaseRowMapper<ManufacturingBatchEntity> {
    @Override
    public ManufacturingBatchEntity apply(Row row, RowMetadata meta) {
        ManufacturingBatchEntity e = ManufacturingBatchEntity.builder()
                .batchId(row.get("batch_id",       String.class))
                .shiftId(row.get("shift_id",       String.class))
                .productId(row.get("product_id",   String.class))
                .plannedQty(row.get("planned_qty", Integer.class))
                .actualQty(row.get("actual_qty",   Integer.class))
                .rejectedQty(row.get("rejected_qty", Integer.class))
                .notes(row.get("notes",            String.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}