package com.hst.materialmgmt.objectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hs.api.model.Email;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.EmailEntity;

public class EmailMapper {

  public static BaseEntity toEntity(Email email) {
    email.setEmailId(BaseMapper.populateId(email.getEmailId()));
    return EmailEntity.builder().emailId(email.getEmailId()).email(email.getEmail()).build();
  }

  public static Email toModel(EmailEntity entity) {
    return new Email().emailId(entity.getEmailId()).email(entity.getEmail());
  }

  public static List<Email> toModels(List<BaseEntity> entities) {
    if (entities == null) return new ArrayList<>();
    return entities.stream()
        .map(entity -> toModel((EmailEntity) entity))
        .collect(Collectors.toList());
  }

  public static List<BaseEntity> toEntities(List<Email> models) {
    if (models == null) return new ArrayList<>();
    return models.stream().map(model -> toEntity((Email) model)).collect(Collectors.toList());
  }
}