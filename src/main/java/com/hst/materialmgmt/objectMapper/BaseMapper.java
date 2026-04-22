package com.hst.materialmgmt.objectMapper;

import java.time.LocalDate;
import java.util.UUID;

import com.hst.materialmgmt.entity.BaseEntity;

public abstract class BaseMapper {

  public abstract BaseEntity toEntity(Object modelObject, Object entityObject, boolean isNew);

  public abstract Object toModel(Object entityObject);

  public static String populateId(String id) {
    if (id == null || id.isEmpty()) {
      UUID uuid = UUID.randomUUID();
      id = uuid.toString();
    }
    return id;
  }

  protected int parseInt(String intString) {
    int value = 0;
    try {
      value = Integer.parseInt(intString);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return value;
  }

  protected float parseFloat(String floatString) {
    float value = 0.0f;
    try {
      value = Float.parseFloat(floatString);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return value;
  }

  public static LocalDate stringToDate(String dateString) {
    if (dateString == null || dateString.isEmpty()) return null;
    return LocalDate.parse(dateString);
  }

  public static String dateToString(LocalDate date) {
    if (date == null) return null;
    return date.toString();
  }
}
