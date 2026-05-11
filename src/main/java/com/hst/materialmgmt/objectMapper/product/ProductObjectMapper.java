package com.hst.materialmgmt.objectMapper.product;

import org.springframework.stereotype.Component;

import com.hst.api.model.Product;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.product.ProductEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;

@Component
public class ProductObjectMapper extends BaseMapper {

	@Override
	public BaseEntity toEntity(Object modelObject, Object entityObject, boolean isNew) {
		Product product = (Product) modelObject;
	    ProductEntity updateEntity = null;

	    if (isNew || entityObject == null) {
	      updateEntity = new ProductEntity();
	    } else {
	      updateEntity = (ProductEntity) entityObject;
	    }
		updateEntity.setProductId(product.getProductId());
	    updateEntity.setSku(product.getSku());
	    updateEntity.setName(product.getName());
	    updateEntity.setCategoryId(product.getCategoryId());
	    updateEntity.setUom(product.getUom());
	    updateEntity.setIsActive(product.getIsActive());
	    return updateEntity;
	}

	@Override
	public Object toModel(Object entityObject) {
		ProductEntity productEntity = (ProductEntity) entityObject;
	    Product product = new Product();

	    product.setProductId(productEntity.getProductId());
	    product.setSku(productEntity.getSku());
	    product.setName(productEntity.getName());
	    product.setCategoryId(productEntity.getCategoryId());
	    product.setUom(productEntity.getUom());
	    product.setIsActive(productEntity.getIsActive());

	    return product;
	}
}
