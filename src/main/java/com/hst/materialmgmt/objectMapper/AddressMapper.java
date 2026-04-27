package com.hst.materialmgmt.objectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hst.api.model.Address;
import com.hst.materialmgmt.entity.AddressEntity;
import com.hst.materialmgmt.entity.BaseEntity;

public class AddressMapper {
  public static BaseEntity toEntity(Address address) {
	address.setAddressId(BaseMapper.populateId(address.getAddressId()));
    return AddressEntity.builder()
        .addressId(address.getAddressId())
        .addressLine1(address.getAddressLine1())
        .addressLine2(address.getAddressLine2())
        .poBoxNumber(address.getPoBoxNumber())
        .city(address.getCity())
        .stateCode(address.getStateCode())
        .postalCode(address.getPostalCode())
        .countryCode(address.getCountryCode())
        .timeZone(address.getTimeZone())
        .build();
  }

  public static Address toModel(AddressEntity entity) {
    return new Address()
        .addressId(entity.getAddressId())
        .addressLine1(entity.getAddressLine1())
        .addressLine2(entity.getAddressLine2())
        .poBoxNumber(entity.getPoBoxNumber())
        .postalCode(entity.getPostalCode())
        .city(entity.getCity())
        .stateCode(entity.getStateCode())
        .timeZone(entity.getTimeZone())
        .countryCode(entity.getCountryCode());
  }

  public static List<Address> toModels(List<BaseEntity> entities) {
    if (entities == null) return new ArrayList<>();
    return entities.stream()
        .map(entity -> toModel((AddressEntity) entity))
        .collect(Collectors.toList());
  }

  public static List<BaseEntity> toEntities(List<Address> models) {
    if (models == null) return new ArrayList<>();
    return models.stream().map(model -> toEntity((Address) model)).collect(Collectors.toList());
  }
}
