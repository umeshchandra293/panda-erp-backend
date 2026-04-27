package com.hst.materialmgmt.rowMapper;

import com.hst.materialmgmt.entity.MaterialOrderDetailEntity;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

public class MaterialOrderItemRowMapper extends BaseRowMapper<MaterialOrderDetailEntity> {

    @Override
	public MaterialOrderDetailEntity apply(Row row, RowMetadata rowMetadata) {
		MaterialOrderDetailEntity entity =
			MaterialOrderDetailEntity.builder()
			.orderId(row.get("order_id", String.class))
            .materialId(row.get("material_id", String.class))
            .orderQuantity(row.get("order_quantity", Double.class))
            .unitPrice(row.get("unit_price", Double.class))
            .qtyReceived(row.get("qty_received", Double.class))
            .lineTotal(row.get("line_total", Double.class))
            .status(row.get("status", String.class))
            .build();
	    populateAuditInfo(entity, row);
	    return entity;
	}
}
