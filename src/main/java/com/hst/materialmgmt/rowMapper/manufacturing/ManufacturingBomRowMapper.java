package com.hst.materialmgmt.rowMapper.manufacturing;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.manufacturing.ManufacturingBomEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ManufacturingBomRowMapper extends BaseRowMapper<ManufacturingBomEntity> {
    @Override
    public ManufacturingBomEntity apply(Row row, RowMetadata meta) {
        ManufacturingBomEntity e = ManufacturingBomEntity.builder()
                .bomId(row.get("bom_id",           String.class))
                .productId(row.get("product_id",   String.class))
                .materialId(row.get("material_id", String.class))
                .qtyPerUnit(row.get("qty_per_unit", BigDecimal.class))
                .uom(row.get("uom",                String.class))
                .notes(row.get("notes",            String.class))
                .isActive(row.get("is_active",     Boolean.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}