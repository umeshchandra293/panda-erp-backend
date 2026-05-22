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
    order_quantity   DECIMAL(10, 4) NOT NULL,
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


-- ── Sequences ────────────────────────────────────────────────────────────────
CREATE SEQUENCE IF NOT EXISTS rm_material_schema.manufacturing_shift_seq
    START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS rm_material_schema.manufacturing_batch_seq
    START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS rm_material_schema.manufacturing_bom_seq
    START WITH 1 INCREMENT BY 1;

-- ── BOM table ────────────────────────────────────────────────────────────────
-- Defines how much of each raw material is needed per bottle of a product
CREATE TABLE IF NOT EXISTS rm_material_schema.manufacturing_bom_tbl (
    bom_id          VARCHAR(40)    NOT NULL PRIMARY KEY,
    product_id      VARCHAR(40)    NOT NULL,  -- FK to product_tbl
    material_id     VARCHAR(40)    NOT NULL,  -- FK to rm_material_tbl
    qty_per_unit    DECIMAL(12,6)  NOT NULL,  -- qty of material per 1 bottle
    uom             VARCHAR(20)    NOT NULL,  -- KG, PCS, LTR etc
    notes           VARCHAR(255),
    is_active       BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40)    NOT NULL,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40)    NOT NULL,
    UNIQUE (product_id, material_id)
);

-- ── Shift header table ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rm_material_schema.manufacturing_shift_tbl (
    shift_id        VARCHAR(40)    NOT NULL PRIMARY KEY,
    shift_date      DATE           NOT NULL,
    shift_type      VARCHAR(20)    NOT NULL,  -- MORNING, EVENING, NIGHT
    operator_name   VARCHAR(100),
    status          VARCHAR(20)    NOT NULL DEFAULT 'CONFIRMED',
    notes           VARCHAR(500),
    total_units     INT            NOT NULL DEFAULT 0,
    total_rejected  INT            NOT NULL DEFAULT 0,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40)    NOT NULL,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40)    NOT NULL
);

-- ── Batch table (one row per product per shift) ──────────────────────────────
CREATE TABLE IF NOT EXISTS rm_material_schema.manufacturing_batch_tbl (
    batch_id        VARCHAR(40)    NOT NULL PRIMARY KEY,
    shift_id        VARCHAR(40)    NOT NULL,
    product_id      VARCHAR(40)    NOT NULL,
    planned_qty     INT            DEFAULT 0,
    actual_qty      INT            NOT NULL DEFAULT 0,
    rejected_qty    INT            NOT NULL DEFAULT 0,
    notes           VARCHAR(255),
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40)    NOT NULL,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40)    NOT NULL,
    FOREIGN KEY (shift_id) REFERENCES rm_material_schema.manufacturing_shift_tbl(shift_id)
        ON DELETE CASCADE
);