package com.hst.materialmgmt.objectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hs.api.model.RawMaterial;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.RawMaterialEntity;

@Component
public class RawMaterialMapper extends BaseMapper {

    @Override
    public BaseEntity toEntity(Object model, Object entity, boolean isNew) {
        RawMaterial mat = (RawMaterial) model;

        RawMaterialEntity e = (entity != null)
                ? (RawMaterialEntity) entity
                : new RawMaterialEntity();

        // Preserve existing ID on update; on create it will be set by the service
        if (mat.getMaterialId() != null && !mat.getMaterialId().isBlank()) {
            e.setMaterialId(mat.getMaterialId());
        }

        e.setMaterialName(mat.getMaterialName());
        e.setDescription(mat.getDescription());

        if (mat.getCategory() != null) {
            e.setCategory(mat.getCategory().name());
        }

        if (mat.getUom() != null) {
            e.setUom(mat.getUom().name());
        }

        e.setHsnSacCode(mat.getHsnSacCode());

        e.setReorderLevel(mat.getReorderLevel() != null
                ? BigDecimal.valueOf(mat.getReorderLevel()) : BigDecimal.ZERO);

        e.setSafetyStockLevel(mat.getSafetyStockLevel() != null
                ? BigDecimal.valueOf(mat.getSafetyStockLevel()) : BigDecimal.ZERO);

        e.setIsActive(mat.getIsActive() != null ? mat.getIsActive() : Boolean.TRUE);

        return e;
    }

    @Override
    public Object toModel(Object entity) {
        if (entity == null) return null;

        RawMaterialEntity e = (RawMaterialEntity) entity;
        RawMaterial mat = new RawMaterial();

        mat.setMaterialId(e.getMaterialId());
        mat.setMaterialName(e.getMaterialName());
        mat.setDescription(e.getDescription());

        if (e.getCategory() != null) {
            mat.setCategory(RawMaterial.CategoryEnum.fromValue(e.getCategory()));
        }

        if (e.getUom() != null) {
            mat.setUom(RawMaterial.UomEnum.fromValue(e.getUom()));
        }

        mat.setHsnSacCode(e.getHsnSacCode());

        if (e.getReorderLevel() != null) {
            mat.setReorderLevel(e.getReorderLevel().doubleValue());
        }

        if (e.getSafetyStockLevel() != null) {
            mat.setSafetyStockLevel(e.getSafetyStockLevel().doubleValue());
        }

        mat.setIsActive(e.getIsActive());

        return mat;
    }
}