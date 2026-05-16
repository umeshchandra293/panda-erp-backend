package com.hst.materialmgmt.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.PhoneEntity;
import com.hst.materialmgmt.entity.supplier.SupplierEntity;
import com.hst.materialmgmt.entity.supplier.SupplierPhoneEntity;
import com.hst.materialmgmt.rowMapper.BaseRowMapper;
import com.hst.materialmgmt.rowMapper.PhoneRowMapper;

import reactor.core.publisher.Mono;

@Repository
public class PhoneRepository extends ParentRepositoryImpl {

  private static final String TABLE_NAME = "rm_phone_tbl";
  private static final String TABLE_NAME_KEY = "phone_id";

  @Override
  protected String getTableName() {
	  return TABLE_NAME;
  }

  @Override
  protected Map<String, Object> getKeyParamMap(String id) {
	  Map<String, Object> keyParams = Map.of(TABLE_NAME_KEY, id);
	  return keyParams;
  }

  @Autowired 
  private PhoneRowMapper phoneRowMapper;

  @SuppressWarnings("unchecked")
  @Override
  protected Class<PhoneEntity> getEntityClass() {
	  return PhoneEntity.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected BaseRowMapper<PhoneEntity> getRowMapper() {
	  return phoneRowMapper;
  }

  	@Override
  	protected String getSelectAllByParentSQL(BaseEntity entity) {
  		String linkTable = null;
  		if (entity instanceof SupplierEntity) linkTable = RepositoryConstants.SUPPLIER_PHONE_LINK;

  		if (linkTable == null) return null;

  		return getSQLforLinkTable(
  			getTableNameWithQualifier(TABLE_NAME),
  			getTableNameWithQualifier(linkTable),
  			TABLE_NAME_KEY);
  	}

  @Override
  public Mono<BaseEntity> saveLink(BaseEntity entity) {
	  if (entity instanceof SupplierPhoneEntity)
		  return create(entity, RepositoryConstants.SUPPLIER_PHONE_ENTITY_CLASS);
    else
      return Mono.error(
          new IllegalArgumentException("Unsupported entity type for saving phone link"));
  	}
}
