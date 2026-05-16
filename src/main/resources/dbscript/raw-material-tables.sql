CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_material_category_tbl (
    category_id      VARCHAR(40) NOT NULL PRIMARY KEY,
    category_name    VARCHAR(256) NOT NULL,
    description      VARCHAR(256) NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40) NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_material_tbl (
	material_id  	    VARCHAR(40) NOT NULL PRIMARY KEY,
	material_name    	VARCHAR(256),
	description      	VARCHAR(256),
	category_id      	VARCHAR(40),
	unit_of_measure  	VARCHAR(40),
	reorder_level    	INT DEFAULT 100,
	safety_stock_level 	INT DEFAULT 50,
	preferred_suppl_id  VARCHAR(40),
	is_active           BOOLEAN DEFAULT TRUE,
	created_at       	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       	VARCHAR(40) NOT NULL,
    updated_at       	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       	VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_location_tbl (
	location_id  	    VARCHAR(40) NOT NULL PRIMARY KEY,
	location_name    	VARCHAR(256),
	description      	VARCHAR(256),
	warehouse_section   VARCHAR(50),
	capacity            DECIMAL(12, 2),
	storage_type        VARCHAR(50) DEFAULT 'Shelf',
	is_active           BOOLEAN DEFAULT TRUE,
	created_at       	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       	VARCHAR(40) NOT NULL,
    updated_at       	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       	VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_material_inventory_tbl (
	inventory_id  	    VARCHAR(40) NOT NULL PRIMARY KEY,
	material_id  	    VARCHAR(40) NOT NULL,
	location_id  	    VARCHAR(40) NOT NULL,
	quantity_on_hand    DECIMAL(12, 2) NOT NULL DEFAULT 0,	
	lot_number          VARCHAR(100),
    expiry_date         DATE,
    date_received       DATE,
    condition           VARCHAR(50) DEFAULT 'Good',
    last_inv_check      TIMESTAMP,
	created_at       	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       	VARCHAR(40) NOT NULL,
    updated_at       	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       	VARCHAR(40) NOT NULL,
    FOREIGN KEY (material_id) REFERENCES "rm_material_schema".rm_material_tbl(material_id) ON DELETE RESTRICT,
    FOREIGN KEY (location_id) REFERENCES "rm_material_schema".rm_location_tbl(location_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_supplier_material_price_tbl (
    price_id    		VARCHAR(40) NOT NULL PRIMARY KEY,
    supplier_code    	VARCHAR(40) NOT NULL,
    material_id    	    VARCHAR(40) NOT NULL,
    unit_price          DECIMAL(10, 4) NOT NULL,
    minimum_Ord_Qty     DECIMAL(12, 2) DEFAULT 1,
    effective_date      DATE NOT NULL,
    expiry_date         DATE,
    currency_code       VARCHAR(3) DEFAULT 'INR',
    is_active           BOOLEAN DEFAULT TRUE,
    created_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     		VARCHAR(40) NOT NULL,
    updated_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by     		VARCHAR(40) NOT NULL,
    FOREIGN KEY (supplier_code) REFERENCES "rm_material_schema".rm_supplier_tbl(supplier_code) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES "rm_material_schema".rm_material_tbl(material_id) ON DELETE CASCADE
);

-- Create the raw material table matching the YAML
CREATE TABLE IF NOT EXISTS rm_material_schema.rm_material_tbl (
    material_id         VARCHAR(40)  NOT NULL PRIMARY KEY,
    material_name       VARCHAR(255),
    description         VARCHAR(500),
    category            VARCHAR(50),
    uom                 VARCHAR(20),
    hsn_sac_code        VARCHAR(20),
    reorder_level       DECIMAL(12,2) DEFAULT 0,
    safety_stock_level  DECIMAL(12,2) DEFAULT 0,
    is_active           BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(40) NOT NULL,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          VARCHAR(40) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS rm_material_schema.material_code_seq
    START WITH 1 INCREMENT BY 1;