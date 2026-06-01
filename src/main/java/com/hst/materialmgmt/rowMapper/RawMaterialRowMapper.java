package com.hst.materialmgmt.rowMapper;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.RawMaterialEntity;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class RawMaterialRowMapper extends BaseRowMapper<RawMaterialEntity> {

    @Override
    public RawMaterialEntity apply(Row row, RowMetadata rowMetadata) {
        RawMaterialEntity entity = RawMaterialEntity.builder()
                .materialId(row.get("material_id",       String.class))
                .materialName(row.get("material_name",   String.class))
                .description(row.get("description",      String.class))
                .category(row.get("category",            String.class))
                .uom(row.get("uom",                      String.class))
                .hsnSacCode(row.get("hsn_sac_code",      String.class))
                .reorderLevel(row.get("reorder_level",   BigDecimal.class))
                .safetyStockLevel(row.get("safety_stock_level", BigDecimal.class))
                .unitPrice(row.get("unit_price",         BigDecimal.class))
                .isActive(row.get("is_active",           Boolean.class))
                .build();

        populateAuditInfo(entity, row);
        return entity;
    }
}