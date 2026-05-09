package com.hst.materialmgmt.production.rowMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.production.entity.FgStockEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class FgStockRowMapper extends BaseRowMapper<FgStockEntity> {
    @Override
    public FgStockEntity apply(Row row, RowMetadata meta) {
        FgStockEntity e = FgStockEntity.builder()
                .fgId(row.get("fg_id",           String.class))
                .productId(row.get("product_id", String.class))
                .quantity(row.get("quantity",     BigDecimal.class))
                .lastUpdated(row.get("last_updated", LocalDateTime.class))
                .build();
        return e;
    }
}