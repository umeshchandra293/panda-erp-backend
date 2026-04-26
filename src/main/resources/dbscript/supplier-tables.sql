CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_supplier_tbl (
    supplier_code    	VARCHAR(40) NOT NULL PRIMARY KEY,
    supplier_name    	VARCHAR(255) NOT NULL,
    supplier_category 	VARCHAR(50),
    supplier_group   	VARCHAR(40),
    legal_entity_id 	VARCHAR(40),
    gst_number     		VARCHAR(15),
    pan_number     		VARCHAR(10),
    effective_date 		DATE NOT NULL,
    end_date       		DATE,
    payment_term   		VARCHAR(20),
    created_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     		VARCHAR(40) NOT NULL,
    updated_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by     		VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_supplier_address_link (
    parent_id   VARCHAR(40) NOT NULL,
    child_id    VARCHAR(40) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40) NOT NULL,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40) NOT NULL,
    PRIMARY KEY (parent_id, child_id),
    FOREIGN KEY (parent_id) REFERENCES "rm_material_schema".rm_supplier_tbl(supplier_code) ON DELETE CASCADE,
    FOREIGN KEY (child_id)  REFERENCES "rm_material_schema".rm_address_tbl(address_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_supplier_phone_link (
    parent_id   VARCHAR(40) NOT NULL,
    child_id    VARCHAR(40) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40) NOT NULL,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40) NOT NULL,
    PRIMARY KEY (parent_id, child_id),
    FOREIGN KEY (parent_id) REFERENCES "rm_material_schema".rm_supplier_tbl(supplier_code) ON DELETE CASCADE,
    FOREIGN KEY (child_id)  REFERENCES "rm_material_schema".rm_phone_tbl(phone_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_supplier_email_link (
    parent_id   VARCHAR(40) NOT NULL,
    child_id    VARCHAR(40) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40) NOT NULL,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40) NOT NULL,
    PRIMARY KEY (parent_id, child_id),
    FOREIGN KEY (parent_id) REFERENCES "rm_material_schema".rm_supplier_tbl(supplier_code) ON DELETE CASCADE,
    FOREIGN KEY (child_id)  REFERENCES "rm_material_schema".rm_email_tbl(email_id) ON DELETE CASCADE
);