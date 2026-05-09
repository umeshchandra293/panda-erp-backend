package com.hst.materialmgmt.objectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hs.api.model.SupplierMaterialMapping;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.SupplierMaterialMapEntity;

@Component
public class SupplierMaterialMapMapper extends BaseMapper {

    private static final String DEFAULT_CURRENCY = "INR";

    @Override
    public BaseEntity toEntity(Object model, Object entity, boolean isNew) {
        SupplierMaterialMapping mapping = (SupplierMaterialMapping) model;

        SupplierMaterialMapEntity updateEntity = (entity != null)
                ? (SupplierMaterialMapEntity) entity
                : new SupplierMaterialMapEntity();

        // Generate a new mappingId only when creating
        if (isNew || updateEntity.getMappingId() == null) {
            updateEntity.setMappingId(populateId(null));
        }

        // supplierCode comes from the path param — set by the service before calling toEntity
        if (mapping.getSupplierCode() != null) {
            updateEntity.setSupplierCode(mapping.getSupplierCode());
        }

        updateEntity.setMaterialId(mapping.getMaterialId());

        if (mapping.getAgreedPrice() != null) {
            updateEntity.setAgreedPrice(BigDecimal.valueOf(mapping.getAgreedPrice()));
        }

        if (mapping.getUom() != null) {
            updateEntity.setUom(mapping.getUom().name());
        }

        if (mapping.getPackSize() != null) {
            updateEntity.setPackSize(BigDecimal.valueOf(mapping.getPackSize()));
        }

        if (mapping.getPackUom() != null) {
            updateEntity.setPackUom(mapping.getPackUom().name());
        }

        if (mapping.getMinOrderQty() != null) {
            updateEntity.setMinOrderQty(BigDecimal.valueOf(mapping.getMinOrderQty()));
        } else {
            updateEntity.setMinOrderQty(BigDecimal.ONE);
        }

        updateEntity.setHsnSacCode(mapping.getHsnSacCode());

        if (mapping.getGstRate() != null) {
            updateEntity.setGstRate(BigDecimal.valueOf(mapping.getGstRate()));
        }

        updateEntity.setLeadTimeDays(mapping.getLeadTimeDays());

        updateEntity.setCurrencyCode(
                mapping.getCurrencyCode() != null ? mapping.getCurrencyCode() : DEFAULT_CURRENCY);

        LocalDate effective = mapping.getEffectiveDate();
        if (effective == null && isNew) {
            effective = LocalDate.now();
        }
        updateEntity.setEffectiveDate(effective);
        updateEntity.setExpiryDate(mapping.getExpiryDate());

        updateEntity.setIsActive(
                mapping.getIsActive() != null ? mapping.getIsActive() : Boolean.TRUE);

        return updateEntity;
    }

    @Override
    public Object toModel(Object entity) {
        if (entity == null) return null;

        SupplierMaterialMapEntity e = (SupplierMaterialMapEntity) entity;
        SupplierMaterialMapping model = new SupplierMaterialMapping();

        model.setMappingId(e.getMappingId());
        model.setSupplierCode(e.getSupplierCode());
        model.setMaterialId(e.getMaterialId());

        if (e.getAgreedPrice() != null) {
            model.setAgreedPrice(e.getAgreedPrice().doubleValue());
        }

        if (e.getUom() != null) {
            model.setUom(SupplierMaterialMapping.UomEnum.fromValue(e.getUom()));
        }

        if (e.getPackSize() != null) {
            model.setPackSize(e.getPackSize().doubleValue());
        }

        if (e.getPackUom() != null) {
            model.setPackUom(SupplierMaterialMapping.PackUomEnum.fromValue(e.getPackUom()));
        }

        if (e.getMinOrderQty() != null) {
            model.setMinOrderQty(e.getMinOrderQty().doubleValue());
        }

        model.setHsnSacCode(e.getHsnSacCode());

        if (e.getGstRate() != null) {
            model.setGstRate(e.getGstRate().doubleValue());
        }

        model.setLeadTimeDays(e.getLeadTimeDays());
        model.setCurrencyCode(e.getCurrencyCode());
        model.setEffectiveDate(e.getEffectiveDate());
        model.setExpiryDate(e.getExpiryDate());
        model.setIsActive(e.getIsActive());

        return model;
    }
}