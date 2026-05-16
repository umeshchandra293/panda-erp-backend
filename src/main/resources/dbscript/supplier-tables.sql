-- ============================================================================
-- GRN & STOCK MOVEMENT TABLES
-- Append to: hst-material-service/src/main/resources/dbscript/matrialmgnt-tables.sql
-- ============================================================================

-- Fix material category constraint to include PREFORMS
ALTER TABLE "erp_finance_schema".rm_material_tbl
    DROP CONSTRAINT IF EXISTS chk_material_category;

ALTER TABLE "erp_finance_schema".rm_material_tbl
    ADD CONSTRAINT chk_material_category CHECK (category IN (
        'PREFORMS', 'CHEMICALS', 'PACKAGING', 'BRANDING', 'CAPS',
        'LABELS', 'INGREDIENTS', 'EQUIPMENT', 'CONSUMABLES', 'OTHER'
    ));

-- GRN sequences
DROP SEQUENCE IF EXISTS "erp_finance_schema".grn_code_seq;
DROP SEQUENCE IF EXISTS "erp_finance_schema".grn_item_code_seq;

CREATE SEQUENCE "erp_finance_schema".grn_code_seq
    START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE CACHE 1;

CREATE SEQUENCE "erp_finance_schema".grn_item_code_seq
    START WITH 1 INCREMENT BY 1 MINVALUE 1 NO MAXVALUE CACHE 1;

-- GRN header
DROP TABLE IF EXISTS "erp_finance_schema".rm_grn_item_tbl CASCADE;
DROP TABLE IF EXISTS "erp_finance_schema".rm_grn_tbl      CASCADE;

CREATE TABLE "erp_finance_schema".rm_grn_tbl (
    grn_id          VARCHAR(40)     NOT NULL PRIMARY KEY,
    po_id           VARCHAR(40),
    supplier_code   VARCHAR(40),
    received_date   DATE            NOT NULL DEFAULT CURRENT_DATE,
    invoice_number  VARCHAR(100),
    status          VARCHAR(20)     NOT NULL DEFAULT 'CONFIRMED',
    notes           VARCHAR(500),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40)     NOT NULL DEFAULT 'system',
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40)     NOT NULL DEFAULT 'system',

    CONSTRAINT chk_grn_status CHECK (status IN ('DRAFT','CONFIRMED','CANCELLED'))
);

CREATE INDEX idx_grn_received_date ON "erp_finance_schema".rm_grn_tbl (received_date);
CREATE INDEX idx_grn_supplier      ON "erp_finance_schema".rm_grn_tbl (supplier_code);

-- GRN line items
CREATE TABLE "erp_finance_schema".rm_grn_item_tbl (
    grn_item_id     VARCHAR(40)     NOT NULL PRIMARY KEY,
    grn_id          VARCHAR(40)     NOT NULL,
    material_id     VARCHAR(40)     NOT NULL,
    ordered_qty     NUMERIC(12,2),
    received_qty    NUMERIC(12,2)   NOT NULL DEFAULT 0,
    unit_cost       NUMERIC(12,4),
    notes           VARCHAR(500),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40)     NOT NULL DEFAULT 'system',
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40)     NOT NULL DEFAULT 'system',

    CONSTRAINT fk_grn_item_grn
        FOREIGN KEY (grn_id)
        REFERENCES "erp_finance_schema".rm_grn_tbl(grn_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_grn_item_material
        FOREIGN KEY (material_id)
        REFERENCES "erp_finance_schema".rm_material_tbl(material_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_received_qty CHECK (received_qty >= 0)
);

CREATE INDEX idx_grn_item_grn      ON "erp_finance_schema".rm_grn_item_tbl (grn_id);
CREATE INDEX idx_grn_item_material ON "erp_finance_schema".rm_grn_item_tbl (material_id);

-- Stock movements
DROP TABLE IF EXISTS "erp_finance_schema".rm_stock_movement_tbl CASCADE;

CREATE TABLE "erp_finance_schema".rm_stock_movement_tbl (
    movement_id     VARCHAR(40)     NOT NULL PRIMARY KEY,
    material_id     VARCHAR(40)     NOT NULL,
    movement_type   VARCHAR(20)     NOT NULL,
    quantity        NUMERIC(12,2)   NOT NULL,
    unit_cost       NUMERIC(12,4),
    movement_date   DATE            NOT NULL DEFAULT CURRENT_DATE,
    reference_type  VARCHAR(20),
    reference_id    VARCHAR(40),
    reason_code     VARCHAR(30),
    notes           VARCHAR(500),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40)     NOT NULL DEFAULT 'system',
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40)     NOT NULL DEFAULT 'system',

    CONSTRAINT fk_movement_material
        FOREIGN KEY (material_id)
        REFERENCES "erp_finance_schema".rm_material_tbl(material_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_movement_type CHECK (movement_type IN (
        'INBOUND', 'CONSUMPTION', 'WASTAGE', 'ADJUSTMENT'
    )),

    CONSTRAINT chk_movement_qty CHECK (quantity > 0)
);

CREATE INDEX idx_movement_material ON "erp_finance_schema".rm_stock_movement_tbl (material_id);
CREATE INDEX idx_movement_date     ON "erp_finance_schema".rm_stock_movement_tbl (movement_date);
CREATE INDEX idx_movement_type     ON "erp_finance_schema".rm_stock_movement_tbl (movement_type);
CREATE INDEX idx_movement_ref      ON "erp_finance_schema".rm_stock_movement_tbl (reference_id);

-- ============================================================================
-- END GRN & STOCK MOVEMENT
-- ============================================================================
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
