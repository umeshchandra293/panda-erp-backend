-- ============================================================================
-- HS ERP - PURCHASE ORDER TABLES
-- Run in pgAdmin if rm_purchase_order_tbl does not already exist.
-- ============================================================================

SET search_path TO erp_finance_schema;

CREATE TABLE IF NOT EXISTS erp_finance_schema.rm_purchase_order_tbl (
    po_id                   VARCHAR(40)     NOT NULL PRIMARY KEY,
    supplier_code           VARCHAR(40)     NOT NULL,
    order_date              DATE            NOT NULL DEFAULT CURRENT_DATE,
    expected_delivery_date  DATE,
    notes                   VARCHAR(1000),
    total_amount            NUMERIC(14,2)   NOT NULL DEFAULT 0,
    status                  VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(40)     NOT NULL DEFAULT 'Admin',
    updated_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(40)     NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_po_supplier
        FOREIGN KEY (supplier_code)
        REFERENCES erp_finance_schema.rm_supplier_tbl(supplier_code)
        ON DELETE RESTRICT,

    CONSTRAINT chk_po_status CHECK (status IN (
        'DRAFT', 'SUBMITTED', 'APPROVED', 'RECEIVED', 'CANCELLED'
    ))
);

CREATE TABLE IF NOT EXISTS erp_finance_schema.rm_purchase_order_item_tbl (
    item_id         VARCHAR(40)     NOT NULL PRIMARY KEY,
    po_id           VARCHAR(40)     NOT NULL,
    material_id     VARCHAR(40)     NOT NULL,
    quantity        NUMERIC(12,3)   NOT NULL,
    unit_price      NUMERIC(12,2)   NOT NULL,
    line_total      NUMERIC(14,2)   NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40)     NOT NULL DEFAULT 'Admin',
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40)     NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_poi_po
        FOREIGN KEY (po_id)
        REFERENCES erp_finance_schema.rm_purchase_order_tbl(po_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_poi_material
        FOREIGN KEY (material_id)
        REFERENCES erp_finance_schema.rm_material_tbl(material_id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS erp_finance_schema.rm_purchase_order_item_link (
    parent_id   VARCHAR(40) NOT NULL,
    child_id    VARCHAR(40) NOT NULL,
    PRIMARY KEY (parent_id, child_id),
    CONSTRAINT fk_poil_parent FOREIGN KEY (parent_id)
        REFERENCES erp_finance_schema.rm_purchase_order_tbl(po_id) ON DELETE CASCADE,
    CONSTRAINT fk_poil_child  FOREIGN KEY (child_id)
        REFERENCES erp_finance_schema.rm_purchase_order_item_tbl(item_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_po_supplier ON erp_finance_schema.rm_purchase_order_tbl (supplier_code);
CREATE INDEX IF NOT EXISTS idx_poi_po      ON erp_finance_schema.rm_purchase_order_item_tbl (po_id);