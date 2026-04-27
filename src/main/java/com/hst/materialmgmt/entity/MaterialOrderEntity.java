package com.hst.materialmgmt.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Transient;
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
@Table(value = "rm_material_order_tbl", schema = "rm_material_schema")
public class MaterialOrderEntity extends BaseEntity {
	
	@Column("order_id")
	private String orderId;
	
	@Column("order_number")
	private String orderNumber;
	  
	@Column("supplier_code")
	private String supplierCode;
	
	@Column("order_date")
	private LocalDateTime orderDate;
	
	@Column("exptd_deli_date")
	private LocalDate exptdDeliDate;
	  
	@Column("actual_deli_date")
	private LocalDate actualDeliDate;
	
	@Column("status")
	private String status;
	
	@Column("total_amount")
	private double totalAmount;
	
	@Column("ord_created_by")
	private String ordCreatedBy;
	
	@Column("approved_by")
	private String approvedBy;
	
	@Column("notes")
	private String notes;
	
	@Transient 
	private List<BaseEntity> materialOrderItems;

	@Override
	public String getId() {
		return orderId;
	}
}