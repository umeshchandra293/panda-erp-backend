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
        ProductPriceEntity entity = (isNew || entityObject == null)
                ? new ProductPriceEntity()
                : (ProductPriceEntity) entityObject;

        entity.setProductPriceId(productPrice.getProductPriceId());
        entity.setProductId(productPrice.getProductId());
        entity.setBaseUnitPrice(productPrice.getBaseUnitPrice());
        entity.setCurrencyCode(productPrice.getCurrencyCode());
        entity.setEffectiveDate(productPrice.getEffectiveDate());
        if (productPrice.getEndDate() != null && productPrice.getEndDate().isPresent()) {
            entity.setEndDate(productPrice.getEndDate().get());
        }
        return entity;
    }

    @Override
    public Object toModel(Object entityObject) {
        ProductPriceEntity e = (ProductPriceEntity) entityObject;
        ProductPrice p = new ProductPrice();
        p.setProductPriceId(e.getProductPriceId());
        p.setProductId(e.getProductId());
        p.setBaseUnitPrice(e.getBaseUnitPrice());
        p.setCurrencyCode(e.getCurrencyCode());
        p.setEffectiveDate(e.getEffectiveDate());
        p.setEndDate(JsonNullable.of(e.getEndDate()));
        return p;
    }
}
