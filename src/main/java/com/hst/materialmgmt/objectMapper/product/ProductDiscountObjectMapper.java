package com.hst.materialmgmt.objectMapper.product;

import org.springframework.stereotype.Component;
import com.hst.api.model.ProductDiscount;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.product.ProductDiscountEntity;
import com.hst.materialmgmt.objectMapper.BaseMapper;

@Component
public class ProductDiscountObjectMapper extends BaseMapper {

	@Override
	public BaseEntity toEntity(Object modelObject, Object entityObject, boolean isNew) {
		ProductDiscount productDiscount = (ProductDiscount) modelObject;
	    ProductDiscountEntity updateEntity = null;

	    if (isNew || entityObject == null) {
	      updateEntity = new ProductDiscountEntity();
	    } else {
	      updateEntity = (ProductDiscountEntity) entityObject;
	    }

		updateEntity.setDiscountId(productDiscount.getDiscountId());
		updateEntity.setProductId(productDiscount.getProductId());
	    updateEntity.setName(productDiscount.getName());
		updateEntity.setDiscountType(productDiscount.getDiscountType());
		updateEntity.setValue(productDiscount.getValue());
		updateEntity.setEffectiveDate(productDiscount.getEffectiveDate());

		return updateEntity;
	}

	@Override
	public Object toModel(Object entityObject) {
		ProductDiscountEntity productDiscountEntity = (ProductDiscountEntity) entityObject;
	    ProductDiscount productDiscount = new ProductDiscount();
	    productDiscount.setDiscountId(productDiscountEntity.getDiscountId());
	    productDiscount.setProductId(productDiscountEntity.getProductId());
	    productDiscount.setName(productDiscountEntity.getName());
	    productDiscount.setDiscountType(productDiscountEntity.getDiscountType());
	    productDiscount.setValue(productDiscountEntity.getValue());
	    productDiscount.setEffectiveDate(productDiscountEntity.getEffectiveDate());
	    return productDiscount;
	}
}