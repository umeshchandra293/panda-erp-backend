package com.hst.materialmgmt.production.rowMapper;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.production.entity.ProductionShiftEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ProductionShiftRowMapper extends BaseRowMapper<ProductionShiftEntity> {
    @Override
    public ProductionShiftEntity apply(Row row, RowMetadata meta) {
        ProductionShiftEntity e = ProductionShiftEntity.builder()
                .shiftId(row.get("shift_id",      String.class))
                .shiftDate(row.get("shift_date",  LocalDate.class))
                .shiftType(row.get("shift_type",  String.class))
                .operatorName(row.get("operator_name", String.class))
                .notes(row.get("notes",           String.class))
                .status(row.get("status",         String.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}
