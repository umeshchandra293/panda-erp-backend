package com.hst.materialmgmt.objectMapper.product;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import com.hst.api.model.ProductCategory;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.product.ProductCategoryEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;

@Component
public class ProductCategoryObjectMapper extends BaseMapper {

	@Override
	public BaseEntity toEntity(Object modelObject, Object entityObject, boolean isNew) {
	
		ProductCategory productCategory = (ProductCategory) modelObject;
	    ProductCategoryEntity updateEntity = null;

	    if (isNew || entityObject == null) {
	      updateEntity = new ProductCategoryEntity();
	    } else {
	      updateEntity = (ProductCategoryEntity) entityObject;
	    }

		updateEntity.setCategoryId(productCategory.getCategoryId());
		updateEntity.setParentId(productCategory.getParentId().get());
	    updateEntity.setName(productCategory.getName());
		updateEntity.setDescription(productCategory.getDescription());		

	    return updateEntity;
	}

	@Override
	public Object toModel(Object entityObject) {

		ProductCategoryEntity productCategoryEntity = (ProductCategoryEntity) entityObject;
	    ProductCategory productCategory = new ProductCategory();
	    productCategory.setCategoryId(productCategoryEntity.getCategoryId());
	    productCategory.setParentId(JsonNullable.of(productCategoryEntity.getParentId()));
	    productCategory.setName(productCategoryEntity.getName());
	    productCategory.setDescription(productCategoryEntity.getDescription());
	    return productCategory;
	}

}
