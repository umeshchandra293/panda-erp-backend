CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_material_usage_tbl (
    usage_id         VARCHAR(40) NOT NULL PRIMARY KEY,
    material_id      VARCHAR(40) NOT NULL,
    inventory_id     VARCHAR(40) NOT NULL,
    usage_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    quantity_used    DECIMAL(12, 2) NOT NULL,
    department       VARCHAR(100),
    used_by          VARCHAR(100),
    purpose          VARCHAR(100) DEFAULT 'Production',
    project_id       VARCHAR(50),
    notes            VARCHAR(1024),
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40) NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40) NOT NULL,
    FOREIGN KEY (material_id) REFERENCES "rm_material_schema".rm_material_tbl(material_id) ON DELETE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES "rm_material_schema".rm_material_inventory_tbl(inventory_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_inventory_adjustment_tbl (
    adjustment_id    VARCHAR(40) NOT NULL PRIMARY KEY,
    inventory_id     VARCHAR(40) NOT NULL,    
    adjustment_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    adjustment_type  VARCHAR(100),
    qty_adjusted     DECIMAL(12, 2) NOT NULL,
    reason_code      VARCHAR(50),
    description      VARCHAR(100),
    adjusted_by      VARCHAR(100) NOT NULL,
    approved         BOOLEAN DEFAULT FALSE,
    approved_by      VARCHAR(100),
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40) NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40) NOT NULL,
    FOREIGN KEY (inventory_id) REFERENCES "rm_material_schema".rm_material_inventory_tbl(inventory_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_inventory_auditing_tbl (
    audit_log_id    VARCHAR(40) NOT NULL PRIMARY KEY,
    inventory_id    VARCHAR(40) NOT NULL,
    material_id     VARCHAR(40) NOT NULL,
    previous_qty    DECIMAL(12, 2),
    new_quantity    DECIMAL(12, 2),
    change_type     VARCHAR(50) DEFAULT 'Adjusted',
    changed_by      VARCHAR(100),
    change_reason   VARCHAR(255),
    changed_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40) NOT NULL,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40) NOT NULL,
    FOREIGN KEY (inventory_id) REFERENCES "rm_material_schema".rm_material_inventory_tbl(inventory_id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES "rm_material_schema".rm_material_tbl(material_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_reorder_point_tbl (
    reorder_id    		VARCHAR(40) NOT NULL PRIMARY KEY,
    material_id     	VARCHAR(40) NOT NULL,
    minimum_stock_level DECIMAL(12, 2) NOT NULL,
    maximum_stock_level DECIMAL(12, 2),
    reorder_quantity    DECIMAL(12, 2) NOT NULL,
    safety_stock        DECIMAL(12, 2) DEFAULT 0,
    lead_time_days      INT DEFAULT 7,
    last_reorder_date   TIMESTAMP,
    effective_date      DATE DEFAULT CURRENT_DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(40) NOT NULL,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(40) NOT NULL,
    FOREIGN KEY (material_id) REFERENCES "rm_material_schema".rm_material_tbl(material_id) ON DELETE CASCADE
);