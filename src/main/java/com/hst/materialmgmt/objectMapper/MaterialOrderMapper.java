package com.hst.materialmgmt.objectMapper;

import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import com.hst.api.model.MaterialOrder;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.MaterialOrderEntity;

@Component
public class MaterialOrderMapper extends BaseMapper {

	@Override
	  public BaseEntity toEntity(Object model, Object entity, boolean isNew) {
	    MaterialOrder materialOrder = (MaterialOrder) model;

	    MaterialOrderEntity updateEntity = null;
	    if (isNew || entity == null) {
	      updateEntity = new MaterialOrderEntity();
	    } else {
	      updateEntity = (MaterialOrderEntity) entity;
	    }

	    updateEntity.setOrderId(materialOrder.getOrderId());
		updateEntity.setOrderNumber(materialOrder.getOrderNumber());
		updateEntity.setSupplierCode(materialOrder.getSupplierCode());
		updateEntity.setOrderDate(materialOrder.getOrderDate().toLocalDateTime());
		updateEntity.setExptdDeliDate(materialOrder.getExptdDeliDate());
		updateEntity.setActualDeliDate(materialOrder.getActualDeliDate());
		updateEntity.setStatus(materialOrder.getStatus());
		updateEntity.setTotalAmount(materialOrder.getTotalAmount().doubleValue());
		updateEntity.setOrdCreatedBy(materialOrder.getOrdCreatedBy());
		updateEntity.setApprovedBy(materialOrder.getApprovedBy());
		updateEntity.setNotes(materialOrder.getNotes());
	    
		
		
		
	    return updateEntity;
	  }

	  @Override
	  public Object toModel(Object entity) {
	    if (entity == null) {
	      return null;
	    }
	    MaterialOrderEntity materialOrderEntity = (MaterialOrderEntity) entity;
	    MaterialOrder materialOrder = new MaterialOrder();
		materialOrder.setOrderId(materialOrderEntity.getOrderId());
		materialOrder.setOrderNumber(materialOrderEntity.getOrderNumber());
		materialOrder.setSupplierCode(materialOrderEntity.getSupplierCode());
		materialOrder.setOrderDate(materialOrderEntity.getOrderDate().atOffset(ZoneOffset.UTC));
		materialOrder.setExptdDeliDate(materialOrderEntity.getExptdDeliDate());
		materialOrder.setActualDeliDate(materialOrderEntity.getActualDeliDate());
		materialOrder.setStatus(materialOrderEntity.getStatus());
		materialOrder.setTotalAmount(materialOrderEntity.getTotalAmount());
		materialOrder.setOrdCreatedBy(materialOrderEntity.getOrdCreatedBy());
		materialOrder.setApprovedBy(materialOrderEntity.getApprovedBy());
		materialOrder.setNotes(materialOrderEntity.getNotes());

	    return materialOrder;
	  }
}
