package com.hst.materialmgmt.rowMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.StockMovementEntity;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class StockMovementRowMapper extends BaseRowMapper<StockMovementEntity> {
    @Override
    public StockMovementEntity apply(Row row, RowMetadata meta) {
        StockMovementEntity e = StockMovementEntity.builder()
                .movementId(row.get("movement_id",     String.class))
                .materialId(row.get("material_id",     String.class))
                .movementType(row.get("movement_type", String.class))
                .quantity(row.get("quantity",          BigDecimal.class))
                .unitCost(row.get("unit_cost",         BigDecimal.class))
                .movementDate(row.get("movement_date", LocalDate.class))
                .referenceType(row.get("reference_type", String.class))
                .referenceId(row.get("reference_id",   String.class))
                .reasonCode(row.get("reason_code",     String.class))  // ← was missing
                .notes(row.get("notes",                String.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}