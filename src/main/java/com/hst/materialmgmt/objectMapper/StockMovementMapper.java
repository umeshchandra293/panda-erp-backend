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

        // ── Safe enum conversions — unknown values stored in DB are skipped
        // rather than crashing the entire response stream.

        if (e.getMovementType() != null) {
            try {
                m.setMovementType(StockMovement.MovementTypeEnum.fromValue(e.getMovementType()));
            } catch (IllegalArgumentException ex) {
                // Unknown movement type — leave null, still return the movement
            }
        }

        if (e.getQuantity() != null) {
            m.setQuantity(e.getQuantity().doubleValue());
        }
        if (e.getUnitCost() != null) {
            m.setUnitCost(e.getUnitCost().doubleValue());
        }

        m.setMovementDate(e.getMovementDate());

        if (e.getReferenceType() != null) {
            try {
                m.setReferenceType(StockMovement.ReferenceTypeEnum.fromValue(e.getReferenceType()));
            } catch (IllegalArgumentException ex) {
                // Unknown reference type — skip
            }
        }

        m.setReferenceId(e.getReferenceId());

        if (e.getReasonCode() != null) {
            try {
                m.setReasonCode(StockMovement.ReasonCodeEnum.fromValue(e.getReasonCode()));
            } catch (IllegalArgumentException ex) {
                // Unknown reason code (e.g. 'MANUAL', 'CORRECTION', 'PHYSICAL_COUNT')
                // stored via manual adjust — skip rather than crash
            }
        }

        m.setNotes(e.getNotes());

        return m;
    }
}