package com.hst.materialmgmt.rowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

public class GenericEntityRowMapper<T> implements RowMapper<T> {

    private final Class<T> entityClass;

    public GenericEntityRowMapper(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T map(Row row, RowMetadata metadata) {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            Class<?> currentClass = entityClass;

            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    field.setAccessible(true);

                    if (shouldIgnore(field)) continue;

                    String columnName = getColumnName(field);

                    // ✅ Safe check before reading column
                    if (hasColumn(metadata, columnName)) {
                        Object value = row.get(columnName, field.getType());
                        if (value != null) {
                            field.set(entity, value);
                        }
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
            return entity;

        } catch (Exception e) {
            throw new RuntimeException("Mapping failed for " + entityClass.getSimpleName(), e);
        }
    }

    private String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            String colValue = field.getAnnotation(Column.class).value();
            if (colValue != null && !colValue.isBlank()) {
                return colValue;
            }
        }
        return camelToSnake(field.getName());
    }

    // ✅ Fixed — getColumnNames() removed, use getColumnMetadatas() only
    private boolean hasColumn(RowMetadata metadata, String columnName) {
        return metadata.getColumnMetadatas()
                       .stream()
                       .anyMatch(m -> m.getName().equalsIgnoreCase(columnName));
    }

    private boolean shouldIgnore(Field field) {
        return Modifier.isStatic(field.getModifiers())
                || Modifier.isTransient(field.getModifiers())
                || field.isAnnotationPresent(Transient.class);
    }

    private String camelToSnake(String camel) {
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.ENGLISH);
    }
}