package com.hst.materialmgmt.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hst.materialmgmt.entity.BaseEntity;
import com.hst.materialmgmt.entity.EmailEntity;
import com.hst.materialmgmt.entity.SupplierEmailEntity;
import com.hst.materialmgmt.entity.SupplierEntity;
import com.hst.materialmgmt.rowMapper.EmailRowMapper;

import reactor.core.publisher.Mono;

@Repository
public class EmailRepository extends ParentRepositoryImpl {

  private static final String TABLE_NAME = "rm_email_tbl";
  private static final String TABLE_NAME_KEY = "email_id";

  @Autowired 
  private EmailRowMapper emailRowMapper;

  @Override
  protected String getTableName() {
    return TABLE_NAME;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<EmailEntity> getEntityClass() {
    return EmailEntity.class;
  }

  @Override
  protected Map<String, Object> getKeyParamMap(String id) {
	  Map<String, Object> keyParams = Map.of(TABLE_NAME_KEY, id);
	  return keyParams;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected EmailRowMapper getRowMapper() {
	  return emailRowMapper;
  }

  @Override
  protected String getSelectAllByParentSQL(BaseEntity entity) {
    String linkTable = null;
    if (entity instanceof SupplierEntity) linkTable = RepositoryConstants.SUPPLIER_EMAIL_LINK;

    if (linkTable == null) return null;

    return getSQLforLinkTable(
        getTableNameWithQualifier(TABLE_NAME),
        getTableNameWithQualifier(linkTable),
        TABLE_NAME_KEY);
  }

  	@Override
  	public Mono<BaseEntity> saveLink(BaseEntity entity) {
  		if (entity instanceof SupplierEmailEntity)
  			return create(entity, RepositoryConstants.SUPPLIER_EMAIL_ENTITY_CLASS);
  		else
  			return Mono.error(new IllegalArgumentException("Unsupported entity type for saving email link"));
  	}
}