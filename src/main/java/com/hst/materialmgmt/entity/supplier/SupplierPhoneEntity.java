package com.hst.materialmgmt.entity.supplier;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.hst.materialmgmt.entity.BaseEntity;

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
@Table(value = "rm_supplier_phone_link", schema = "rm_material_schema")
public class SupplierPhoneEntity extends BaseEntity {
	
	@Id
	@Column("parent_id")
	private String parentId;

	@Column("child_id")
	private String childId;

	public String getId() {
		return parentId + "-" + childId;
	}
}
