package com.hst.materialmgmt.production.rowMapper;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.production.entity.ProductionBomEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ProductionBomRowMapper extends BaseRowMapper<ProductionBomEntity> {
    @Override
    public ProductionBomEntity apply(Row row, RowMetadata meta) {
        ProductionBomEntity e = ProductionBomEntity.builder()
                .bomId(row.get("bom_id",         String.class))
                .productId(row.get("product_id", String.class))
                .materialId(row.get("material_id", String.class))
                .qtyPerUnit(row.get("qty_per_unit", BigDecimal.class))
                .uom(row.get("uom",              String.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}
