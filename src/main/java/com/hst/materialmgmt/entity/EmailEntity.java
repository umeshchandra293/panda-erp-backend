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
@Table(value = "rm_email_tbl", schema = "rm_material_schema")
public class EmailEntity extends BaseEntity {

	@Column("email_id")
	private String emailId;
	
	@Column("email")
	private String email;
	
	public String getId() {
	    return emailId;
	}
}
