package com.hst.materialmgmt.rowMapper.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.product.ProductCategoryEntity;
import com.hst.materialmgmt.entity.product.ProductDiscountEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ProductDiscountRowMapper extends BaseRowMapper<ProductDiscountEntity> {

	@Override
	public ProductDiscountEntity apply(Row row, RowMetadata u) {
		ProductDiscountEntity entity =
			ProductDiscountEntity.builder()
			.discountId(row.get("discount_id", String.class))
			.productId(row.get("product_id", String.class))
			.discountType(row.get("discount_type", String.class))
			.value(row.get("value", BigDecimal.class))
			.minQuantity(row.get("min_quantity", Integer.class))
			.effectiveDate(row.get("effective_date", LocalDate.class))
			.endDate(row.get("end_date", LocalDate.class))
			.build();
		populateAuditInfo(entity, row);
		return entity;
	}
}

