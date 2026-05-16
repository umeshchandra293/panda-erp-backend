package com.hst.materialmgmt.entity.company;

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
@Table(value = "company_tbl", schema = "rm_material_schema")
public class CompanyEntity extends BaseEntity {
	
	@Id 
	@Column("company_id")
	private String companyId;

	@Column("name")
	private String name;

	@Column("legal_name")
	private String legalName;

	@Column("tax_id")
	private String taxId;

	@Column("email_id")
	private String emailId;

	@Column("phone")
	private String phone;

	@Column("website")
	private String website;

	@Column("address")
	private String address;

	@Override
	public String getId() {
		return companyId;
	}
}
