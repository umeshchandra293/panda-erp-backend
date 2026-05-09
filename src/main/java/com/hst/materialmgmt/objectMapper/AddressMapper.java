package com.hst.materialmgmt.objectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hs.api.model.Address;
import com.hst.materialmgmt.entity.AddressEntity;
import com.hst.materialmgmt.entity.BaseEntity;

public class AddressMapper {

    private static final String DEFAULT_COUNTRY_CODE = "IN";

    public static BaseEntity toEntity(Address address) {
        address.setAddressId(BaseMapper.populateId(address.getAddressId()));

        return AddressEntity.builder()
                .addressId(address.getAddressId())
                .addressType(address.getAddressType() != null ? address.getAddressType().name() : null)
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .poBoxNumber(address.getPoBoxNumber())
                .city(address.getCity())
                .stateCode(address.getStateCode())
                .postalCode(address.getPostalCode())
                .countryCode(address.getCountryCode() != null ? address.getCountryCode() : DEFAULT_COUNTRY_CODE)
                .timeZone(address.getTimeZone())
                .isPrimary(address.getIsPrimary() != null ? address.getIsPrimary() : Boolean.FALSE)
                .build();
    }

    public static Address toModel(AddressEntity entity) {
        Address address = new Address()
                .addressId(entity.getAddressId())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .poBoxNumber(entity.getPoBoxNumber())
                .city(entity.getCity())
                .stateCode(entity.getStateCode())
                .postalCode(entity.getPostalCode())
                .countryCode(entity.getCountryCode())
                .timeZone(entity.getTimeZone())
                .isPrimary(entity.getIsPrimary());

        if (entity.getAddressType() != null) {
            address.setAddressType(Address.AddressTypeEnum.fromValue(entity.getAddressType()));
        }
        return address;
    }

    public static List<Address> toModels(List<BaseEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(entity -> toModel((AddressEntity) entity))
                .collect(Collectors.toList());
    }

    public static List<BaseEntity> toEntities(List<Address> models) {
        if (models == null) return new ArrayList<>();
        return models.stream()
                .map(AddressMapper::toEntity)
                .collect(Collectors.toList());
    }
}