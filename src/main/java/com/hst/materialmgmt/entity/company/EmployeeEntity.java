package com.hst.materialmgmt.entity.company;

import java.time.LocalDate;

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
@Table(value = "employee_tbl", schema = "rm_material_schema")
public class EmployeeEntity extends BaseEntity {
	
	@Id 
	@Column("employee_id")
	private String employeeId;

	@Column("company_id")
	private String companyId;

	@Column("department_id")
	private String departmentId;

	@Column("employee_code")
	private String employeeCode;

	@Column("first_name")
	private String firstName;

	@Column("last_name")
	private String lastName;

	@Column("email")
	private String email;

	@Column("phone")
	private String phone;

	@Column("job_title")
	private String jobTitle;

	@Column("manager_id")
	private String managerId;

	@Column("hire_date")
	private LocalDate hireDate;

	@Column("termination_date")
	private LocalDate terminationDate;

	@Column("status")
	private String status;
	
	@Override
	public String getId() {
		return employeeId;
	}
}
