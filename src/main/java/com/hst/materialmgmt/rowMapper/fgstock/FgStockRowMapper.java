package com.hst.materialmgmt.rowMapper.fgstock;

import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.fgstock.FgStockEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class FgStockRowMapper extends BaseRowMapper<FgStockEntity> {
    @Override
    public FgStockEntity apply(Row row, RowMetadata meta) {
        FgStockEntity e = new FgStockEntity();
        e.setFgId(row.get("fg_id", String.class));
        e.setProductId(row.get("product_id", String.class));
        e.setQuantity(row.get("quantity", Integer.class));
        populateAuditInfo(e, row);
        return e;
    }
}