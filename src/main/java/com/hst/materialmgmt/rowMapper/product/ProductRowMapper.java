package com.hst.materialmgmt.rowMapper.product;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.hst.materialmgmt.entity.product.ProductEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ProductRowMapper extends BaseRowMapper<ProductEntity> {

    @Override
    public ProductEntity apply(Row t, RowMetadata u) {
        ProductEntity entity = ProductEntity.builder()
            .productId(t.get("product_id",             String.class))
            .sku(t.get("sku",                          String.class))
            .name(t.get("name",                        String.class))
            .categoryId(t.get("category_id",           String.class))
            .uom(t.get("uom",                          String.class))
            .isActive(t.get("is_active",               Boolean.class))
            .unitsPerBox(t.get("units_per_box",        Integer.class))
            .costPerCase(t.get("cost_per_case",        BigDecimal.class))
            .sellingPricePerCase(t.get("selling_price_per_case", BigDecimal.class))
            .build();
        populateAuditInfo(entity, t);
        return entity;
    }
}