-- ─────────────────────────────────────────────────────────────
-- Production Module Tables
-- ─────────────────────────────────────────────────────────────

-- Bill of Materials
CREATE TABLE IF NOT EXISTS erp_finance_schema.production_bom_tbl (
    bom_id          VARCHAR(40)    PRIMARY KEY,
    product_id      VARCHAR(40)    NOT NULL,
    material_id     VARCHAR(40)    NOT NULL,
    qty_per_unit    NUMERIC        NOT NULL,
    uom             VARCHAR(20),
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100)   DEFAULT 'Admin',
    updated_by      VARCHAR(100)   DEFAULT 'Admin'
);

-- Production shift header
CREATE TABLE IF NOT EXISTS erp_finance_schema.production_shift_tbl (
    shift_id        VARCHAR(40)    PRIMARY KEY,
    shift_date      DATE           NOT NULL,
    shift_type      VARCHAR(20)    NOT NULL CHECK (shift_type IN ('MORNING','EVENING','NIGHT')),
    operator_name   VARCHAR(100),
    notes           VARCHAR(500),
    status          VARCHAR(20)    DEFAULT 'DRAFT',
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100)   DEFAULT 'Admin',
    updated_by      VARCHAR(100)   DEFAULT 'Admin'
);

-- Production batch per product per shift
CREATE TABLE IF NOT EXISTS erp_finance_schema.production_batch_tbl (
    batch_id        VARCHAR(40)    PRIMARY KEY,
    shift_id        VARCHAR(40)    NOT NULL REFERENCES erp_finance_schema.production_shift_tbl(shift_id),
    product_id      VARCHAR(40)    NOT NULL,
    planned_qty     NUMERIC        DEFAULT 0,
    actual_qty      NUMERIC        NOT NULL DEFAULT 0,
    rejected_qty    NUMERIC        DEFAULT 0,
    notes           VARCHAR(500),
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100)   DEFAULT 'Admin',
    updated_by      VARCHAR(100)   DEFAULT 'Admin'
);

-- Finished goods current stock
CREATE TABLE IF NOT EXISTS erp_finance_schema.fg_stock_tbl (
    fg_id           VARCHAR(40)    PRIMARY KEY,
    product_id      VARCHAR(40)    NOT NULL UNIQUE,
    quantity        NUMERIC        NOT NULL DEFAULT 0,
    last_updated    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

-- Finished goods movement ledger
CREATE TABLE IF NOT EXISTS erp_finance_schema.fg_stock_movement_tbl (
    movement_id     VARCHAR(40)    PRIMARY KEY,
    product_id      VARCHAR(40)    NOT NULL,
    movement_type   VARCHAR(20)    NOT NULL CHECK (movement_type IN ('PRODUCED','DISPATCHED','DAMAGED','ADJUSTED')),
    quantity        NUMERIC        NOT NULL,
    reference_type  VARCHAR(40),
    reference_id    VARCHAR(40),
    movement_date   DATE           NOT NULL,
    notes           VARCHAR(500),
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100)   DEFAULT 'Admin',
    updated_by      VARCHAR(100)   DEFAULT 'Admin'
);

-- Sequences
CREATE SEQUENCE IF NOT EXISTS erp_finance_schema.shift_seq START 1;
CREATE SEQUENCE IF NOT EXISTS erp_finance_schema.batch_seq START 1;
CREATE SEQUENCE IF NOT EXISTS erp_finance_schema.fg_movement_seq START 1;

-- ─────────────────────────────────────────────────────────────
-- Seed BOM data
-- ─────────────────────────────────────────────────────────────
INSERT INTO erp_finance_schema.production_bom_tbl (bom_id, product_id, material_id, qty_per_unit, uom) VALUES
-- 1L bottle (PROD-002 = 1L box of 12, so per box = 12 bottles)
('BOM-001', 'PROD-002', 'MAT-PB-FG',  12,  'PCS'),  -- PET bottles 1L x12
('BOM-002', 'PROD-002', 'MAT-CAP-28', 12,  'PCS'),  -- caps x12
('BOM-003', 'PROD-002', 'MAT-LBL-1L', 12,  'PCS'),  -- labels x12
-- 500ml bottle (PROD-003 = box of 24)
('BOM-004', 'PROD-003', 'MAT-PB-FG',  24,  'PCS'),
('BOM-005', 'PROD-003', 'MAT-CAP-28', 24,  'PCS'),
('BOM-006', 'PROD-003', 'MAT-LBL-1L', 24,  'PCS'),
-- 250ml bottle (PROD-004 = box of 48)
('BOM-007', 'PROD-004', 'MAT-PB-FG',  48,  'PCS'),
('BOM-008', 'PROD-004', 'MAT-CAP-28', 48,  'PCS'),
('BOM-009', 'PROD-004', 'MAT-LBL-1L', 48,  'PCS'),
-- 20L bubble (PROD-001)
('BOM-010', 'PROD-001', 'MAT-MG-FG',  1,   'PCS'),  -- 20L bottle
('BOM-011', 'PROD-001', 'MAT-CA-FG',  1,   'PCS'),  -- cap
('BOM-012', 'PROD-001', 'MAT-SB-FG',  1,   'PCS')   -- shrink band
ON CONFLICT (bom_id) DO NOTHING;

-- Seed FG stock rows (starting at 0)
INSERT INTO erp_finance_schema.fg_stock_tbl (fg_id, product_id, quantity) VALUES
('FG-001', 'PROD-001', 0),
('FG-002', 'PROD-002', 0),
('FG-003', 'PROD-003', 0),
('FG-004', 'PROD-004', 0)
ON CONFLICT (fg_id) DO NOTHING;