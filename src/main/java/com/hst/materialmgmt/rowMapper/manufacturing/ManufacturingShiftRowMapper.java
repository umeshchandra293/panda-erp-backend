package com.hst.materialmgmt.rowMapper.manufacturing;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingShiftEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ManufacturingShiftRowMapper extends BaseRowMapper<ManufacturingShiftEntity> {
    @Override
    public ManufacturingShiftEntity apply(Row row, RowMetadata meta) {
        ManufacturingShiftEntity e = ManufacturingShiftEntity.builder()
                .shiftId(row.get("shift_id",       String.class))
                .shiftDate(row.get("shift_date",   LocalDate.class))
                .shiftType(row.get("shift_type",   String.class))
                .operatorName(row.get("operator_name", String.class))
                .status(row.get("status",          String.class))
                .notes(row.get("notes",            String.class))
                .totalUnits(row.get("total_units", Integer.class))
                .totalRejected(row.get("total_rejected", Integer.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}