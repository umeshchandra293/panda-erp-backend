CREATE TABLE IF NOT EXISTS "rm_material_schema".company_tbl (
	company_id      VARCHAR(40) NOT NULL PRIMARY KEY,
  	name            VARCHAR(200) NOT NULL,
  	legal_name      VARCHAR(250),
  	tax_id          VARCHAR(50),
  	email_id        VARCHAR(254),
  	phone           VARCHAR(50),
  	website         VARCHAR(200),
  	address         VARCHAR(200),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40) NOT NULL,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40) NOT NULL	
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".department_tbl (
	department_id   VARCHAR(40) NOT NULL PRIMARY KEY,
	company_id      VARCHAR(40) NOT NULL,	
  	name            VARCHAR(200) NOT NULL,
  	code            VARCHAR(40) NOT NULL,
  	parent_dept_id  VARCHAR(40),  	
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40) NOT NULL,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40) NOT NULL,
    FOREIGN KEY (company_id) REFERENCES "rm_material_schema".company_tbl(company_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_dept_id) REFERENCES "rm_material_schema".department_tbl(department_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".employee_tbl (
	employee_id     	VARCHAR(40) NOT NULL PRIMARY KEY,
	company_id          VARCHAR(40) NOT NULL,
  	department_id   	VARCHAR(40) NOT NULL,
	employee_code   	VARCHAR(40),
	first_name      	VARCHAR(100) NOT NULL,
  	last_name       	VARCHAR(100) NOT NULL,
  	email           	VARCHAR(254),
  	phone           	VARCHAR(50),
  	job_title       	VARCHAR(150),
  	manager_id 			VARCHAR(40) NOT NULL,
  	hire_date       	DATE,
  	termination_date 	DATE,
  	status          	VARCHAR(30) NOT NULL DEFAULT 'active',
    created_at      	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      	VARCHAR(40) NOT NULL,
    updated_at      	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      	VARCHAR(40) NOT NULL,
    FOREIGN KEY (company_id) REFERENCES "rm_material_schema".company_tbl(company_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES "rm_material_schema".department_tbl(department_id) ON DELETE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES "rm_material_schema".employee_tbl(employee_id) ON DELETE CASCADE	
}
