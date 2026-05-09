package com.hst.materialmgmt.objectMapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.hs.api.model.Supplier;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.SupplierEntity;

@Component
public class SupplierMapper extends BaseMapper {

    private static final String DEFAULT_GST_REG_TYPE = "REGULAR";
    private static final String DEFAULT_COUNTRY_CODE = "IN";
    private static final int DEFAULT_LEAD_TIME_DAYS = 7;

    @Override
    public BaseEntity toEntity(Object model, Object entity, boolean isNew) {
        Supplier vendor = (Supplier) model;

        // Always start from the existing entity if available so the row's PK
        // and audit fields survive a PUT where the request body omits supplierCode.
        SupplierEntity updateEntity = (entity != null)
                ? (SupplierEntity) entity
                : new SupplierEntity();

        // PK: prefer the model's value, but fall back to whatever the entity
        // already has (existing row on update, freshly-generated code on create).
        String code = (vendor.getSupplierCode() != null && !vendor.getSupplierCode().isBlank())
                ? vendor.getSupplierCode()
                : updateEntity.getSupplierCode();
        updateEntity.setSupplierCode(code);

        updateEntity.setSupplierName(vendor.getSupplierName());
        updateEntity.setSupplierCategory(
                vendor.getSupplierCategory() != null ? vendor.getSupplierCategory().name() : null);
        updateEntity.setSupplierGroup(vendor.getSupplierGroup());
        updateEntity.setLegalEntity(vendor.getLegalEntityId());
        updateEntity.setContactPersonName(vendor.getContactPersonName());
        updateEntity.setGstNumber(vendor.getGstNumber());
        updateEntity.setGstRegistrationType(
                vendor.getGstRegistrationType() != null
                        ? vendor.getGstRegistrationType().name()
                        : DEFAULT_GST_REG_TYPE);
        updateEntity.setPanNumber(vendor.getPanNumber());
        updateEntity.setStateCode(vendor.getStateCode());
        updateEntity.setCountryCode(
                vendor.getCountryCode() != null ? vendor.getCountryCode() : DEFAULT_COUNTRY_CODE);
        updateEntity.setLeadTimeDays(
                vendor.getLeadTimeDays() != null ? vendor.getLeadTimeDays() : DEFAULT_LEAD_TIME_DAYS);
        updateEntity.setPaymentTerm(
                vendor.getPaymentTerm() != null ? vendor.getPaymentTerm().name() : null);

        // effectiveDate defaults to today on create per the OpenAPI contract
        LocalDate effective = vendor.getEffectiveDate();
        if (effective == null && isNew) {
            effective = LocalDate.now();
        }
        updateEntity.setEffectiveDate(effective);
        updateEntity.setEndDate(vendor.getEndDate());

        updateEntity.setIsActive(vendor.getIsActive() != null ? vendor.getIsActive() : Boolean.TRUE);

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
        if (vendorEntity.getSupplierCategory() != null) {
            vendor.setSupplierCategory(
                    Supplier.SupplierCategoryEnum.fromValue(vendorEntity.getSupplierCategory()));
        }
        vendor.setSupplierGroup(vendorEntity.getSupplierGroup());
        vendor.setLegalEntityId(vendorEntity.getLegalEntity());
        vendor.setContactPersonName(vendorEntity.getContactPersonName());
        vendor.setGstNumber(vendorEntity.getGstNumber());
        if (vendorEntity.getGstRegistrationType() != null) {
            vendor.setGstRegistrationType(
                    Supplier.GstRegistrationTypeEnum.fromValue(vendorEntity.getGstRegistrationType()));
        }
        vendor.setPanNumber(vendorEntity.getPanNumber());
        vendor.setStateCode(vendorEntity.getStateCode());
        vendor.setCountryCode(vendorEntity.getCountryCode());
        vendor.setLeadTimeDays(vendorEntity.getLeadTimeDays());
        if (vendorEntity.getPaymentTerm() != null) {
            vendor.setPaymentTerm(
                    Supplier.PaymentTermEnum.fromValue(vendorEntity.getPaymentTerm()));
        }
        vendor.setEffectiveDate(vendorEntity.getEffectiveDate());
        vendor.setEndDate(vendorEntity.getEndDate());
        vendor.setIsActive(vendorEntity.getIsActive());

        vendor.setAddresses(AddressMapper.toModels(vendorEntity.getAddressEntities()));
        vendor.setPhones(PhoneMapper.toModels(vendorEntity.getPhoneEntities()));
        vendor.setEmails(EmailMapper.toModels(vendorEntity.getEmailEntities()));

        return vendor;
    }
}