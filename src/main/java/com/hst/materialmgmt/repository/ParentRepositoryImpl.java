package com.hst.materialmgmt.repository;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;

import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.GenericEntityRowMapper;
import com.hst.materialmgmt.rowMapper.RowMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class ParentRepositoryImpl implements ParentRepository {

  public static final String SCHEMA_NAME = "erp_finance_schema.";

  @Autowired protected R2dbcEntityTemplate r2dbcEntityTemplate;

  @Autowired protected DatabaseClient databaseClient;

  protected abstract <T> Class<T> getEntityClass();

  protected abstract <T> BaseRowMapper<T> getRowMapper();

  protected abstract String getTableName();

  protected abstract Map<String, Object> getKeyParamMap(String id);

  protected String getSelectAllByParentSQL(BaseEntity entity) {
    return null;
  }

  private <T> RowMapper<T> getNewRowMapper() {
    GenericEntityRowMapper<T> rowMapper = new GenericEntityRowMapper<>(getEntityClass());
    return rowMapper;
  }

  protected String getTableNameWithQualifier() {
    return SCHEMA_NAME + getTableName();
  }

  protected String getTableNameWithQualifier(String tableName) {
    return SCHEMA_NAME + tableName;
  }

  @Override
  public Mono<BaseEntity> create(BaseEntity entity) {
    return create(entity, getEntityClass());
  }

  @Override
  public Mono<Long> deleteById(String id) {
    if (id == null || id.trim().isEmpty()) {
      return Mono.error(new IllegalArgumentException("id cannot be null or empty"));
    }

    Map<String, Object> keyParams = getKeyParamMap(id);
    String whereClause = getWhereClauseForId(keyParams);
    String sql = "DELETE FROM " + getTableNameWithQualifier() + " WHERE " + whereClause;

    DatabaseClient.GenericExecuteSpec spec = bindParameters(keyParams, sql);

    return spec.fetch().rowsUpdated().map(Long::longValue);
  }

  @Override
  public Mono<Object> findById(String id) {
    Map<String, Object> keyParams = getKeyParamMap(id);
    String whereClause = getWhereClauseForId(keyParams);
    String sql = "SELECT * FROM " + getTableNameWithQualifier() + " WHERE " + whereClause;
    DatabaseClient.GenericExecuteSpec spec = bindParameters(keyParams, sql);
    return spec.map(getNewRowMapper()::map).first();
  }

  @Override
  public Flux<Object> findAll() {
    return databaseClient
        .sql("select * from " + getTableNameWithQualifier())
        .map(getNewRowMapper()::map) // Applies the BiFunction to each row
        .all() // Collects all results into a Flux
        .log("findAll", Level.FINEST); // Reactive logs each signal
  }

@Override
    public Mono<Long> update(String id, BaseEntity entity) {
        if (id == null || id.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Key cannot be null or empty"));
        }
        
        if (entity == null) {
            return Mono.error(new IllegalArgumentException("Entity data cannot be null"));
        }
        
        entity.setUpdateDefaults(); 

        Map<String, Object> keyParams = getKeyParamMap(id);
        String whereClause = getWhereClauseForId(keyParams);

        // This now uses the fixed hierarchical scanner
        Map<String, Object> dataParams = buildDataParams(entity, keyParams.keySet());

        String setClause = getSetClause(dataParams);
        String sql = "UPDATE " + getTableNameWithQualifier() + " SET " + setClause + " WHERE " + whereClause;

        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql);

        // Bind parameters logic...
        for (var e : dataParams.entrySet()) {
            spec = spec.bind(e.getKey(), e.getValue());
        }
        for (var e : keyParams.entrySet()) {
            spec = spec.bind(e.getKey(), e.getValue());
        }

        return spec.fetch().rowsUpdated().map(Long::longValue);
    }

  private Map<String, Object> buildDataParams(Object entity, Set<String> keyFields) {
        Map<String, Object> dataParams = new HashMap<>();
        Class<?> currentClass = entity.getClass();
        
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);

                String fieldName = field.getName();

                // Validation logic
                if (keyFields.contains(fieldName)) continue;
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (Modifier.isTransient(field.getModifiers())) continue;
                if (field.isAnnotationPresent(Transient.class)) continue;

                // Resolve column name (Respects @Column from Problem 4)
                String columnName = fieldName;
                Column column = field.getAnnotation(Column.class);
                if (column != null && !column.value().isBlank()) {
                    columnName = column.value();
                } else {
                    columnName = camelToSnake(fieldName); // Fallback to your convention
                }

                try {
                    Object value = field.get(entity);
                    // Avoid overwriting values if already found in child (shadowing)
                    if (value != null && !dataParams.containsKey(columnName)) {
                        dataParams.put(columnName, value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access field " + fieldName, e);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return dataParams;
    }

  @SuppressWarnings("unchecked")
  public <T> Mono<BaseEntity> create(BaseEntity entity, @SuppressWarnings("rawtypes")   Class clazz) {
    if (entity == null) {
      return Mono.error(new IllegalArgumentException("Entity cannot be null"));
    }

    if (entity.getId() == null || entity.getId().isEmpty()) {
      return Mono.error(new IllegalArgumentException("Entity key cannot be null or empty"));
    }

    entity.setCreateDefaults(); // Set created defaults if not already set
    entity.setUpdateDefaults(); // Set modified defaults

    return r2dbcEntityTemplate.insert(clazz).using(entity).thenReturn(entity);
  }

  @Override
  public Mono<Long> deleteMultiple(List<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return Mono.just(0L); // nothing to delete
    }

    return r2dbcEntityTemplate.delete(Query.query(Criteria.where("id").in(ids)), getEntityClass());
  }

  @Override
  public Flux<Object> findAllByParent(BaseEntity parentEntity) {
    String sql = getSelectAllByParentSQL(parentEntity);
    if (sql == null || sql.isEmpty()) return Flux.empty();

    return databaseClient
        .sql(getSelectAllByParentSQL(parentEntity))
        .bind("parentId", parentEntity.getId())
        .map(getNewRowMapper()::map)
        .all(); // Use .all() to get a stream of all matching rows
  }

  @Override
  public Mono<Long> deleteAllByParent(BaseEntity parentEntity) {
    return r2dbcEntityTemplate.delete(
        Query.query(Criteria.where("parent_id").is(parentEntity.getId())), getEntityClass());
  }

@Override
  public Mono<BaseEntity> saveLink(BaseEntity entity) {
	return Mono.empty();
  }

  protected String getSQLforLinkTable(
      String childTableName, String linkTableName, String childKeyColumn) {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder
        .append("select a.* from ")
        .append(childTableName)
        .append(" a join ")
        .append(linkTableName)
        .append(" b on a." + childKeyColumn + " = b.child_id ")
        .append(" where b.parent_id = :parentId");
    return sqlBuilder.toString();
  }

  private String getWhereClauseForId(Map<String, Object> keyParams) {
    String whereClause =
        keyParams.keySet().stream().map(k -> k + " = :" + k).collect(Collectors.joining(" AND "));
    return whereClause;
  }

  private String getSetClause(Map<String, Object> dataParams) {
    String setClause =
        dataParams.keySet().stream().map(k -> k + " = :" + k).collect(Collectors.joining(", "));
    return setClause;
  }

  private DatabaseClient.GenericExecuteSpec bindParameters(
      Map<String, Object> keyParams, String sql) {
    DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql);
    for (var e : keyParams.entrySet()) {
      spec = spec.bind(e.getKey(), e.getValue());
    }
    return spec;
  }

  private String camelToSnake(String camel) {
        return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(java.util.Locale.ENGLISH);
    }

}
