package com.hst.materialmgmt.rowMapper.product;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.product.ProductDiscountEntity;
import com.hst.materialmgmt.entity.product.ProductPriceEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ProductPriceRowMapper extends BaseRowMapper<ProductPriceEntity>{

	@Override
	public ProductPriceEntity apply(Row row, RowMetadata u) {
		ProductPriceEntity entity =
			ProductPriceEntity.builder()
			.productPriceId(row.get("product_price_id", String.class))
			.productId(row.get("product_id", String.class))
			.baseUnitPrice(row.get("base_unit_price", Double.class))
			.currencyCode(row.get("currency_code", String.class))
			.effectiveDate(row.get("effective_date", LocalDate.class))
			.endDate(row.get("end_date", LocalDate.class))
			.build();
		populateAuditInfo(entity, row);
		return entity;
	}
}
