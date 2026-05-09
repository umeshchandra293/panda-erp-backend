package com.hst.materialmgmt.production.rowMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.production.entity.FgStockMovementEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class FgStockMovementRowMapper extends BaseRowMapper<FgStockMovementEntity> {
    @Override
    public FgStockMovementEntity apply(Row row, RowMetadata meta) {
        FgStockMovementEntity e = FgStockMovementEntity.builder()
                .movementId(row.get("movement_id",   String.class))
                .productId(row.get("product_id",     String.class))
                .movementType(row.get("movement_type", String.class))
                .quantity(row.get("quantity",         BigDecimal.class))
                .referenceType(row.get("reference_type", String.class))
                .referenceId(row.get("reference_id", String.class))
                .movementDate(row.get("movement_date", LocalDate.class))
                .notes(row.get("notes",               String.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}