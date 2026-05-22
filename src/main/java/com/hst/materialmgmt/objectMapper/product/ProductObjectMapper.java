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
        ProductEntity e = (isNew || entityObject == null)
            ? new ProductEntity() : (ProductEntity) entityObject;

        e.setProductId(product.getProductId());
        e.setSku(product.getSku());
        e.setName(product.getName());
        e.setCategoryId(product.getCategoryId());
        e.setUom(product.getUom());
        e.setIsActive(product.getIsActive());
        if (product.getUnitsPerBox() != null)
            e.setUnitsPerBox(product.getUnitsPerBox());
        return e;
    }

    @Override
    public Object toModel(Object entityObject) {
        ProductEntity e = (ProductEntity) entityObject;
        Product product = new Product();
        product.setProductId(e.getProductId());
        product.setSku(e.getSku());
        product.setName(e.getName());
        product.setCategoryId(e.getCategoryId());
        product.setUom(e.getUom());
        product.setIsActive(e.getIsActive());
        product.setUnitsPerBox(e.getUnitsPerBox());
        return product;
    }
}