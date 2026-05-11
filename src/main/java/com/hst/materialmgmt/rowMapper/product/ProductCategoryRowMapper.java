package com.hst.materialmgmt.rowMapper.product;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.company.CompanyEntity;
import com.hst.materialmgmt.entity.product.ProductCategoryEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class ProductCategoryRowMapper extends BaseRowMapper<ProductCategoryEntity> {

	@Override
	public ProductCategoryEntity apply(Row row, RowMetadata u) {
		ProductCategoryEntity entity =
			ProductCategoryEntity.builder()
			.categoryId(row.get("category_id", String.class))
			.parentId(row.get("parent_id", String.class))
			.name(row.get("name", String.class))
			.description(row.get("description", String.class))
			.build();
		populateAuditInfo(entity, row);
		return entity;
	}

}
