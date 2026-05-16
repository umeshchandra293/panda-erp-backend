package com.hst.materialmgmt.rowMapper;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.EmailEntity;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public class EmailRowMapper extends BaseRowMapper<EmailEntity> {
  @Override
  public EmailEntity apply(Row row, RowMetadata rowMetadata) {
    EmailEntity entity =
        EmailEntity.builder()
            .emailId(row.get("email_id", String.class))
            .email(row.get("email", String.class))
            .build();
    populateAuditInfo(entity, row);
    return entity;
  }
}
