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
@Table(value = "rm_phone_tbl", schema = "rm_material_schema")
public class PhoneEntity extends BaseEntity {

	@Column("phone_id")
	private String phoneId;

	@Column("phone_type")
	private String phoneType;

	@Column("phone_number")
	private String phoneNumber;

	@Column("phone_extension")
	private String phoneExtension;
	
	@Column("is_primary")
	private boolean isPrimary;

	public String getId() {
		return phoneId;
	}
}