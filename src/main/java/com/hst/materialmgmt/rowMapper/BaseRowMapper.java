package com.hst.materialmgmt.rowMapper;

import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import com.hst.materialmgmt.entity.BaseEntity;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@Component
public abstract class BaseRowMapper<T> implements BiFunction<Row, RowMetadata, T> {
	public abstract T apply(Row t, RowMetadata u);

	public void populateAuditInfo(BaseEntity entity, Row row) {
		entity.setCreatedBy(row.get("created_by", String.class));
		entity.setCreatedAt(row.get("created_at", java.time.LocalDateTime.class));
		entity.setUpdatedBy(row.get("updated_by", String.class));
		entity.setUpdatedAt(row.get("updated_at", java.time.LocalDateTime.class));
	}
}
