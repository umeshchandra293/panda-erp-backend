CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_material_order_tbl (
    order_id         VARCHAR(40) NOT NULL PRIMARY KEY,
    order_number     VARCHAR(40) NOT NULL,
    supplier_code    VARCHAR(40) NOT NULL,
	order_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	exptd_deli_date  DATE,
	actual_deli_date DATE,
	status           VARCHAR(50) DEFAULT 'Draft',
	total_amount     DECIMAL(12, 2),
	ord_created_by   VARCHAR(100),
    approved_by      VARCHAR(100),
    notes            VARCHAR(1024),
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40) NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40) NOT NULL,
    FOREIGN KEY (supplier_code) REFERENCES "rm_material_schema".rm_supplier_tbl(supplier_code) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_material_order_detail_tbl (
    order_detail_id  VARCHAR(40) NOT NULL PRIMARY KEY,
    order_id         VARCHAR(40) NOT NULL,
    material_id      VARCHAR(40) NOT NULL,
    order_quantity   VARCHAR(40) NOT NULL,
    unit_price       DECIMAL(10, 4) NOT NULL,
    qty_received     DECIMAL(12, 2) DEFAULT 0,
    line_total       DECIMAL(12, 2) NOT NULL,
    status           VARCHAR(50) DEFAULT 'Pending',
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40) NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES "rm_material_schema".rm_material_order_tbl(order_id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES "rm_material_schema".rm_material_tbl(material_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_material_receiving_tbl (
    receiving_id     VARCHAR(40) NOT NULL PRIMARY KEY,
    order_id         VARCHAR(40) NOT NULL,
    receiving_num    VARCHAR(40) NOT NULL,
    receiving_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    
    received_by      VARCHAR(100) NOT NULL,
    qty_received     DECIMAL(12, 2) NOT NULL,
    status           VARCHAR(50) DEFAULT 'Complete',
    notes            VARCHAR(256),
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40) NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES "rm_material_schema".rm_material_order_tbl(order_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_quality_inspection_tbl (
    inspection_id    	VARCHAR(40) NOT NULL PRIMARY KEY,
    receiving_id     	VARCHAR(40) NOT NULL,
    material_id      	VARCHAR(40) NOT NULL,
    inspection_date  	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    inspected_by     	VARCHAR(100) NOT NULL,
    inspection_result 	VARCHAR(50) NOT NULL,
    remarks            	VARCHAR(256),
    certificate_no      VARCHAR(100),
    certificate_url     VARCHAR(255),
    created_at       	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       	VARCHAR(40) NOT NULL,
    updated_at       	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       	VARCHAR(40) NOT NULL,
    FOREIGN KEY (receiving_id) REFERENCES "rm_material_schema".rm_material_receiving_tbl(receiving_id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES "rm_material_schema".rm_material_tbl(material_id) ON DELETE CASCADE
);
