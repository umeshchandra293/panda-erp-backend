package com.hst.materialmgmt.rowMapper;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.GrnItemEntity;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class GrnItemRowMapper extends BaseRowMapper<GrnItemEntity> {
    @Override
    public GrnItemEntity apply(Row row, RowMetadata meta) {
        GrnItemEntity e = GrnItemEntity.builder()
                .grnItemId(row.get("grn_item_id",   String.class))
                .grnId(row.get("grn_id",            String.class))
                .materialId(row.get("material_id",  String.class))
                .orderedQty(row.get("ordered_qty",  BigDecimal.class))
                .receivedQty(row.get("received_qty", BigDecimal.class))
                .unitCost(row.get("unit_cost",      BigDecimal.class))
                .notes(row.get("notes",             String.class))
                .build();
        populateAuditInfo(e, row);
        return e;
    }
}
