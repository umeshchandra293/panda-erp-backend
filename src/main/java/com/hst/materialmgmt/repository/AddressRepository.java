package com.hst.materialmgmt.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.AddressEntity;
import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.supplier.SupplierAddressEntity;
import com.hst.materialmgmt.entity.supplier.SupplierEntity;
import com.hst.materialmgmt.rowMapper.AddressRowMapper;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;

import reactor.core.publisher.Mono;

@Repository
public class AddressRepository extends ParentRepositoryImpl {
  private static final String TABLE_NAME = "rm_address_tbl";
  private static final String TABLE_NAME_KEY = "address_id";

  @Autowired 
  private AddressRowMapper addressRowMapper;

  @Override
  protected String getTableName() {
    return TABLE_NAME;
  }

  @Override
  protected Map<String, Object> getKeyParamMap(String id) {
    Map<String, Object> keyParams = Map.of(TABLE_NAME_KEY, id);
    return keyParams;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected Class<AddressEntity> getEntityClass() {
    return AddressEntity.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected BaseRowMapper<AddressEntity> getRowMapper() {
    return addressRowMapper;
  }

  @Override
  protected String getSelectAllByParentSQL(BaseEntity entity) {
    String linkTable = null;
    if (entity instanceof SupplierEntity) linkTable = RepositoryConstants.SUPPLIER_ADDRESS_LINK;

    if (linkTable == null) return null;

    return getSQLforLinkTable(
        getTableNameWithQualifier(TABLE_NAME),
        getTableNameWithQualifier(linkTable),
        TABLE_NAME_KEY);
  }

  @Override
  public Mono<BaseEntity> saveLink(BaseEntity entity) {
    if (entity instanceof SupplierAddressEntity)
      return create(entity, RepositoryConstants.SUPPLIER_ADDRESS_ENTITY_CLASS);
    else
      return Mono.error(
          new IllegalArgumentException("Unsupported entity type for saving address link"));
  }
}
