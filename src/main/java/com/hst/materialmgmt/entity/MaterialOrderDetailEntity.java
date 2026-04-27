package com.hst.materialmgmt.entity;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
@SuperBuilder
@Table(value = "rm_material_order_detail_tbl", schema = "rm_material_schema")
public class MaterialOrderDetailEntity extends BaseEntity {

	@Column("order_detail_id")
	private String orderDetailId;

	@Column("order_id")
	private String orderId;

	@Column("material_id")
	private String materialId;

	@Column("order_quantity")
	private double orderQuantity;

	@Column("unit_price")
	private double unitPrice;

	@Column("qty_received")
	private double qtyReceived;

	@Column("line_total")
	private double lineTotal;

	@Column("status")
	private String status;

	@Override
	public String getId() {
		return orderDetailId;
	}
}
