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
@Table(value = "department_tbl", schema = "rm_material_schema")
public class DepartmentEntity extends BaseEntity {
	
	@Id 
	@Column("department_id")
	private String departmentId;

	@Column("company_id")
	private String companyId;

	@Column("name")
	private String name;

	@Column("code")
	private String code;

	@Column("parent_dept_id")
	private String parentDeptId;
	
	@Override
	public String getId() {
		return departmentId;
	}
}
