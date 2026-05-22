package com.hst.materialmgmt.rowMapper.fgstock;

import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.fgstock.FgMovementEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class FgMovementRowMapper extends BaseRowMapper<FgMovementEntity> {
    @Override
    public FgMovementEntity apply(Row row, RowMetadata meta) {
        FgMovementEntity e = FgMovementEntity.builder()
                .movementId(row.get("movement_id",   String.class))
                .productId(row.get("product_id",     String.class))
                .movementType(row.get("movement_type", String.class))
                .quantity(row.get("quantity",         Integer.class))
                .referenceId(row.get("reference_id", String.class))
                .notes(row.get("notes",              String.class))
                .movementDate(row.get("movement_date", LocalDate.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}