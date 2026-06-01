package com.hst.materialmgmt.rowMapper.fgstock;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.fgstock.FgDispatchItemEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class FgDispatchItemRowMapper extends BaseRowMapper<FgDispatchItemEntity> {
    @Override
    public FgDispatchItemEntity apply(Row row, RowMetadata meta) {
        FgDispatchItemEntity e = FgDispatchItemEntity.builder()
            .dispatchItemId(row.get("dispatch_item_id",        String.class))
            .dispatchId(row.get("dispatch_id",                 String.class))
            .productId(row.get("product_id",                   String.class))
            .casesDispatched(row.get("cases_dispatched",       Integer.class))
            .bottlesDispatched(row.get("bottles_dispatched",   Integer.class))
            .casesReturned(row.get("cases_returned",           Integer.class))
            .bottlesReturned(row.get("bottles_returned",       Integer.class))
            .sellingPricePerCase(row.get("selling_price_per_case", BigDecimal.class))
            .build();
        populateAuditInfo(e, row);
        return e;
    }
}