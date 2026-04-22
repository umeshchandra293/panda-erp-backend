package com.hst.materialmgmt.rowMapper;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

@FunctionalInterface
public interface RowMapper<T> {
  T map(Row row, RowMetadata metadata);
}
