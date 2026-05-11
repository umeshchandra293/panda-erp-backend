package com.hst.materialmgmt.objectMapper.product;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import com.hst.api.model.ProductPrice;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.product.ProductPriceEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;

@Component
public class ProductPriceObjectMapper extends BaseMapper {

	@Override
	public BaseEntity toEntity(Object modelObject, Object entityObject, boolean isNew) {
		ProductPrice productPrice = (ProductPrice) modelObject;
	    ProductPriceEntity updateEntity = null;

	    if (isNew || entityObject == null) {
	      updateEntity = new ProductPriceEntity();
	    } else {
	      updateEntity = (ProductPriceEntity) entityObject;
	    }
		
		updateEntity.setBasePriceId(productPrice.getProductPriceId());
		updateEntity.setProductId(productPrice.getProductId());
		updateEntity.setBaseUnitPrice(productPrice.getBaseUnitPrice());
		updateEntity.setCurrencyCode(productPrice.getCurrencyCode());
		updateEntity.setEffectiveDate(productPrice.getEffectiveDate());
		updateEntity.setEndDate(productPrice.getEndDate().get());

	    return updateEntity;
	}

	@Override
	public Object toModel(Object entityObject) {
		ProductPriceEntity productPriceEntity = (ProductPriceEntity) entityObject;
	    ProductPrice productPrice = new ProductPrice();

	    productPrice.setProductPriceId(productPriceEntity.getBasePriceId());
	    productPrice.setProductId(productPriceEntity.getProductId());
	    productPrice.setBaseUnitPrice(productPriceEntity.getBaseUnitPrice());
	    productPrice.setCurrencyCode(productPriceEntity.getCurrencyCode());
	    productPrice.setEffectiveDate(productPriceEntity.getEffectiveDate());
	    productPrice.setEndDate(JsonNullable.of(productPriceEntity.getEndDate()));
	    return productPrice;
	}
}
