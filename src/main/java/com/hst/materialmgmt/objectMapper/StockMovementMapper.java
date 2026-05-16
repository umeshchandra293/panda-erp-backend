package com.hst.materialmgmt.objectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hst.api.model.StockMovement;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.StockMovementEntity;

@Component
public class StockMovementMapper extends BaseMapper {

    @Override
    public BaseEntity toEntity(Object model, Object entity, boolean isNew) {
        StockMovement m = (StockMovement) model;

        StockMovementEntity e = (entity != null)
                ? (StockMovementEntity) entity
                : new StockMovementEntity();

        if (isNew && (e.getMovementId() == null || e.getMovementId().isBlank())) {
            // movementId is set by service (via code generator) before calling this
            // — preserve whatever was injected
        }
        if (m.getMovementId() != null && !m.getMovementId().isBlank()) {
            e.setMovementId(m.getMovementId());
        }

        e.setMaterialId(m.getMaterialId());

        if (m.getMovementType() != null) {
            e.setMovementType(m.getMovementType().name());
        }

        if (m.getQuantity() != null) {
            e.setQuantity(BigDecimal.valueOf(m.getQuantity()));
        }

        if (m.getUnitCost() != null) {
            e.setUnitCost(BigDecimal.valueOf(m.getUnitCost()));
        }

        e.setMovementDate(m.getMovementDate() != null ? m.getMovementDate() : LocalDate.now());

        if (m.getReferenceType() != null) {
            e.setReferenceType(m.getReferenceType().name());
        }
        e.setReferenceId(m.getReferenceId());

        if (m.getReasonCode() != null) {
            e.setReasonCode(m.getReasonCode().name());
        }

        e.setNotes(m.getNotes());

        return e;
    }

    @Override
    public Object toModel(Object entity) {
        if (entity == null) return null;

        StockMovementEntity e = (StockMovementEntity) entity;
        StockMovement m = new StockMovement();

        m.setMovementId(e.getMovementId());
        m.setMaterialId(e.getMaterialId());

        if (e.getMovementType() != null) {
            m.setMovementType(StockMovement.MovementTypeEnum.fromValue(e.getMovementType()));
        }
        if (e.getQuantity() != null) {
            m.setQuantity(e.getQuantity().doubleValue());
        }
        if (e.getUnitCost() != null) {
            m.setUnitCost(e.getUnitCost().doubleValue());
        }
        m.setMovementDate(e.getMovementDate());

        if (e.getReferenceType() != null) {
            m.setReferenceType(StockMovement.ReferenceTypeEnum.fromValue(e.getReferenceType()));
        }
        m.setReferenceId(e.getReferenceId());

        if (e.getReasonCode() != null) {
            m.setReasonCode(StockMovement.ReasonCodeEnum.fromValue(e.getReasonCode()));
        }
        m.setNotes(e.getNotes());

        return m;
    }
}
