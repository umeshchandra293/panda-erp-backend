package com.hst.materialmgmt.objectMapper;

import org.springframework.stereotype.Component;

import com.hst.api.model.Supplier;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.supplier.SupplierEntity;

@Component
public class SupplierMapper extends BaseMapper {

	@Override
	  public BaseEntity toEntity(Object model, Object entity, boolean isNew) {
	    Supplier vendor = (Supplier) model;

	    SupplierEntity updateEntity = null;
	    if (isNew || entity == null) {
	      updateEntity = new SupplierEntity();
	    } else {
	      updateEntity = (SupplierEntity) entity;
	    }

	    updateEntity.setSupplierCode(vendor.getSupplierCode());
	    updateEntity.setSupplierName(vendor.getSupplierName());
	    updateEntity.setSupplierCategory(vendor.getSupplierCategory());
	    updateEntity.setSupplierGroup(vendor.getSupplierGroup());
	    updateEntity.setLegalEntity(vendor.getLegalEntityId());
	    updateEntity.setGstNumber(vendor.getGstNumber());
	    updateEntity.setPanNumber(vendor.getPanNumber());
	    updateEntity.setEffectiveDate(vendor.getEffectiveDate());
	    updateEntity.setEndDate(vendor.getEndDate());
	    updateEntity.setPaymentTerm(vendor.getPaymentTerm());
	    updateEntity.setAddressEntities(AddressMapper.toEntities(vendor.getAddresses()));
	    updateEntity.setPhoneEntities(PhoneMapper.toEntities(vendor.getPhones()));
	    updateEntity.setEmailEntities(EmailMapper.toEntities(vendor.getEmails()));
	    return updateEntity;
	  }

	  @Override
	  public Object toModel(Object entity) {
	    if (entity == null) {
	      return null;
	    }
	    SupplierEntity vendorEntity = (SupplierEntity) entity;
	    Supplier vendor = new Supplier();

	    vendor.setSupplierCode(vendorEntity.getSupplierCode());
	    vendor.setSupplierName(vendorEntity.getSupplierName());
	    vendor.setSupplierCategory(vendorEntity.getSupplierCategory());
	    vendor.setSupplierGroup(vendorEntity.getSupplierGroup());
	    vendor.setLegalEntityId(vendorEntity.getLegalEntity());
	    vendor.setGstNumber(vendorEntity.getGstNumber());
	    vendor.setPanNumber(vendorEntity.getPanNumber());
	    vendor.setEffectiveDate(vendorEntity.getEffectiveDate());
	    vendor.setEndDate(vendorEntity.getEndDate());
	    vendor.setPaymentTerm(vendorEntity.getPaymentTerm());
	    vendor.setAddresses(AddressMapper.toModels(vendorEntity.getAddressEntities()));
	    vendor.setPhones(PhoneMapper.toModels(vendorEntity.getPhoneEntities()));
	    vendor.setEmails(EmailMapper.toModels(vendorEntity.getEmailEntities()));
	    return vendor;
	  }
}