package com.hst.materialmgmt.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntrospectionHelper {
  public static Map<Class<?>, List<Object>> getChildrenLists(Object obj)
      throws IllegalAccessException {
    Map<Class<?>, List<Object>> childrenMap = new HashMap<>();

    if (obj == null) {
      return childrenMap;
    }

    Class<?> clazz = obj.getClass();
    for (Field field : clazz.getDeclaredFields()) {
      if (field.getType().equals(List.class)) {
        field.setAccessible(true);
        List<?> childrenList = (List<?>) field.get(obj);
        if (childrenList != null && !childrenList.isEmpty()) {
          // Assuming all objects in the list are of the same type
          childrenMap.put(childrenList.get(0).getClass(), (List<Object>) childrenList);
        }
      }
    }
    return childrenMap;
  }
}