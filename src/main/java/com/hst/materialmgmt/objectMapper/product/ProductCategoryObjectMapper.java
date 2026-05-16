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
        ProductCategory pc = (ProductCategory) modelObject;
        ProductCategoryEntity entity = (isNew || entityObject == null)
                ? new ProductCategoryEntity()
                : (ProductCategoryEntity) entityObject;

        entity.setCategoryId(pc.getCategoryId());
        entity.setName(pc.getName());
        entity.setDescription(pc.getDescription());

        // Safe null check for JsonNullable parentId
        if (pc.getParentId() != null && pc.getParentId().isPresent()) {
            entity.setParentId(pc.getParentId().get());
        } else {
            entity.setParentId(null);
        }
        return entity;
    }

    @Override
    public Object toModel(Object entityObject) {
        if (entityObject == null) return null;
        ProductCategoryEntity e = (ProductCategoryEntity) entityObject;
        ProductCategory pc = new ProductCategory();
        pc.setCategoryId(e.getCategoryId());
        pc.setName(e.getName());
        pc.setDescription(e.getDescription());
        pc.setParentId(JsonNullable.of(e.getParentId()));
        return pc;
    }
}
