package com.hst.materialmgmt.objectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hs.api.model.Phone;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.PhoneEntity;

public class PhoneMapper {

  public static BaseEntity toEntity(Phone phone) {
    phone.setPhoneId(BaseMapper.populateId(phone.getPhoneId()));
    return PhoneEntity.builder()
        .phoneId(phone.getPhoneId())
        .phoneType(phone.getPhoneType())
        .phoneNumber(phone.getPhoneNumber())
        .phoneExtension(phone.getPhoneExtension())
        .isPrimary(phone.getIsPrimary())
        .build();
  }

  public static Phone toModel(PhoneEntity entity) {
    return new Phone()
        .phoneId(entity.getPhoneId())
        .phoneType(entity.getPhoneType())
        .phoneNumber(entity.getPhoneNumber())
        .isPrimary(entity.isPrimary())
        .phoneExtension(entity.getPhoneExtension());
  }

  public static List<Phone> toModels(List<BaseEntity> entities) {
    if (entities == null) return new ArrayList<>();
    return entities.stream()
        .map(entity -> toModel((PhoneEntity) entity))
        .collect(Collectors.toList());
  }

  public static List<BaseEntity> toEntities(List<Phone> models) {
    if (models == null) return new ArrayList<>();
    return models.stream().map(model -> toEntity((Phone) model)).collect(Collectors.toList());
  }
}