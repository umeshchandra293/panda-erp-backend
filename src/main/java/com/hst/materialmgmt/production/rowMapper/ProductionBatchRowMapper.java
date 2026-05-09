package com.hst.materialmgmt.production.rowMapper;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.production.entity.ProductionBatchEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ProductionBatchRowMapper extends BaseRowMapper<ProductionBatchEntity> {
    @Override
    public ProductionBatchEntity apply(Row row, RowMetadata meta) {
        ProductionBatchEntity e = ProductionBatchEntity.builder()
                .batchId(row.get("batch_id",      String.class))
                .shiftId(row.get("shift_id",      String.class))
                .productId(row.get("product_id",  String.class))
                .plannedQty(row.get("planned_qty", BigDecimal.class))
                .actualQty(row.get("actual_qty",  BigDecimal.class))
                .rejectedQty(row.get("rejected_qty", BigDecimal.class))
                .notes(row.get("notes",           String.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}