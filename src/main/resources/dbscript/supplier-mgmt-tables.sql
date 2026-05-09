-- ============================================================================
-- HS ERP - SUPPLIER MANAGEMENT TABLES
-- PostgreSQL / R2DBC
-- Last updated: 2026-04-25
--
-- Fresh-start migration. Drops existing supplier tables and the address/phone/
-- email + link tables (since address now has address_type) and recreates them.
-- Also creates the new rm_supplier_material_map_tbl that powers the PO form
-- material dropdown and the SupplierDetailPage "Map Material" feature.
--
-- Run order:
--   1. This script (drops + recreates everything supplier-related)
-- ============================================================================

SET search_path TO erp_finance_schema;

-- ============================================================================
-- 1. DROP (in dependency order: links → mapping → children → parent)
-- ============================================================================

DROP TABLE IF EXISTS erp_finance_schema.rm_supplier_material_map_tbl CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.rm_supplier_address_link    CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.rm_supplier_phone_link      CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.rm_supplier_email_link      CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.rm_address_tbl              CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.rm_phone_tbl                CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.rm_email_tbl                CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.rm_supplier_tbl             CASCADE;

DROP SEQUENCE IF EXISTS erp_finance_schema.supplier_code_seq;

-- ============================================================================
-- 2. SEQUENCE for server-generated supplier codes (SUPP-000001, SUPP-000002...)
-- ============================================================================

CREATE SEQUENCE erp_finance_schema.supplier_code_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

-- ============================================================================
-- 3. SHARED CHILD TABLES (address / phone / email)
-- ----------------------------------------------------------------------------
-- These are referenced by suppliers via link tables (many-to-many pattern).
-- The same address/phone/email row could in principle be linked from multiple
-- parents, but in practice each supplier has its own — the link-table pattern
-- is kept for consistency with the existing architecture.
-- ============================================================================

CREATE TABLE erp_finance_schema.rm_address_tbl (
    address_id       VARCHAR(40)  NOT NULL PRIMARY KEY,
    address_type     VARCHAR(20)  NOT NULL,    -- REGISTERED / FACTORY / BILLING / SHIPPING
    address_line_1   VARCHAR(100) NOT NULL,
    address_line_2   VARCHAR(100),
    po_box_number    VARCHAR(20),
    city             VARCHAR(60)  NOT NULL,
    state_cd         VARCHAR(40)  NOT NULL,
    postal_cd        VARCHAR(20)  NOT NULL,
    country_cd       VARCHAR(20)  NOT NULL DEFAULT 'IN',
    time_zone        VARCHAR(20),
    is_primary       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40)  NOT NULL,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40)  NOT NULL,
    CONSTRAINT chk_address_type CHECK
        (address_type IN ('REGISTERED','FACTORY','BILLING','SHIPPING'))
);

CREATE INDEX idx_address_type ON erp_finance_schema.rm_address_tbl (address_type);

CREATE TABLE erp_finance_schema.rm_phone_tbl (
    phone_id         VARCHAR(40)  NOT NULL PRIMARY KEY,
    phone_type       VARCHAR(10)  NOT NULL,    -- WORK / MOBILE / FAX / HOME
    phone_number     VARCHAR(20)  NOT NULL,
    phone_extension  VARCHAR(6),
    is_primary       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40)  NOT NULL,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40)  NOT NULL,
    CONSTRAINT chk_phone_type CHECK
        (phone_type IN ('WORK','MOBILE','FAX','HOME'))
);

CREATE TABLE erp_finance_schema.rm_email_tbl (
    email_id         VARCHAR(40)  NOT NULL PRIMARY KEY,
    email            VARCHAR(255) NOT NULL,
    is_primary       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40)  NOT NULL,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40)  NOT NULL
);

-- NOTE: The previous schema had a UNIQUE(email) constraint, which the existing
-- frontend was working around by generating fake "blank-NNN@test.com" values
-- when the field was empty. With the rebuild, emails are optional at the
-- supplier level, so uniqueness is dropped — two suppliers can legitimately
-- share a generic email. Add a unique partial index here later if you want
-- to enforce it for non-null emails only.

-- ============================================================================
-- 4. SUPPLIER MASTER
-- ============================================================================

CREATE TABLE erp_finance_schema.rm_supplier_tbl (
    supplier_code           VARCHAR(40)  NOT NULL PRIMARY KEY,
    supplier_name           VARCHAR(255) NOT NULL,
    supplier_category       VARCHAR(50)  NOT NULL,
    supplier_group          VARCHAR(40),
    legal_entity_id         VARCHAR(40),
    contact_person_name     VARCHAR(100),
    gst_number              VARCHAR(15),
    gst_registration_type   VARCHAR(20)  NOT NULL DEFAULT 'REGULAR',
    pan_number              VARCHAR(10),
    state_code              VARCHAR(2)   NOT NULL,    -- 2-digit GST state code: "27", "36", etc.
    country_code            VARCHAR(20)  NOT NULL DEFAULT 'IN',
    lead_time_days          INTEGER      NOT NULL DEFAULT 7,
    payment_term            VARCHAR(20)  NOT NULL,
    effective_date          DATE         NOT NULL DEFAULT CURRENT_DATE,
    end_date                DATE,
    is_active               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by              VARCHAR(40)  NOT NULL,
    updated_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by              VARCHAR(40)  NOT NULL,

    CONSTRAINT chk_supplier_category CHECK (supplier_category IN
        ('CHEMICALS','PACKAGING','BRANDING','CAPS','LABELS',
         'INGREDIENTS','EQUIPMENT','SERVICES','OTHER')),

    CONSTRAINT chk_gst_reg_type CHECK (gst_registration_type IN
        ('REGULAR','COMPOSITION','UNREGISTERED')),

    CONSTRAINT chk_payment_term CHECK (payment_term IN
        ('ADVANCE_100','ADVANCE_50','NET_15','NET_30','NET_45','NET_60','COD')),

    CONSTRAINT chk_state_code_format CHECK (state_code ~ '^[0-9]{2}$'),

    CONSTRAINT chk_gst_format CHECK
        (gst_number IS NULL OR
         gst_number ~ '^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$'),

    CONSTRAINT chk_pan_format CHECK
        (pan_number IS NULL OR pan_number ~ '^[A-Z]{5}[0-9]{4}[A-Z]{1}$'),

    CONSTRAINT chk_lead_time CHECK (lead_time_days >= 0),

    CONSTRAINT chk_date_range CHECK (end_date IS NULL OR end_date >= effective_date)
);

CREATE INDEX idx_supplier_active   ON erp_finance_schema.rm_supplier_tbl (is_active);
CREATE INDEX idx_supplier_category ON erp_finance_schema.rm_supplier_tbl (supplier_category);
CREATE INDEX idx_supplier_state    ON erp_finance_schema.rm_supplier_tbl (state_code);

-- ============================================================================
-- 5. LINK TABLES (supplier ↔ address/phone/email)
-- ============================================================================

CREATE TABLE erp_finance_schema.rm_supplier_address_link (
    parent_id   VARCHAR(40) NOT NULL,
    child_id    VARCHAR(40) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40) NOT NULL,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40) NOT NULL,
    PRIMARY KEY (parent_id, child_id),
    FOREIGN KEY (parent_id) REFERENCES erp_finance_schema.rm_supplier_tbl(supplier_code) ON DELETE CASCADE,
    FOREIGN KEY (child_id)  REFERENCES erp_finance_schema.rm_address_tbl(address_id)    ON DELETE CASCADE
);

CREATE TABLE erp_finance_schema.rm_supplier_phone_link (
    parent_id   VARCHAR(40) NOT NULL,
    child_id    VARCHAR(40) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40) NOT NULL,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40) NOT NULL,
    PRIMARY KEY (parent_id, child_id),
    FOREIGN KEY (parent_id) REFERENCES erp_finance_schema.rm_supplier_tbl(supplier_code) ON DELETE CASCADE,
    FOREIGN KEY (child_id)  REFERENCES erp_finance_schema.rm_phone_tbl(phone_id)        ON DELETE CASCADE
);

CREATE TABLE erp_finance_schema.rm_supplier_email_link (
    parent_id   VARCHAR(40) NOT NULL,
    child_id    VARCHAR(40) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40) NOT NULL,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40) NOT NULL,
    PRIMARY KEY (parent_id, child_id),
    FOREIGN KEY (parent_id) REFERENCES erp_finance_schema.rm_supplier_tbl(supplier_code) ON DELETE CASCADE,
    FOREIGN KEY (child_id)  REFERENCES erp_finance_schema.rm_email_tbl(email_id)        ON DELETE CASCADE
);

-- ============================================================================
-- 6. SUPPLIER ↔ MATERIAL MAPPING
-- ----------------------------------------------------------------------------
-- This is the missing piece that breaks the PO form's material dropdown.
-- One row per (supplier, material) pair, holding the negotiated terms.
-- ============================================================================

CREATE TABLE erp_finance_schema.rm_supplier_material_map_tbl (
    mapping_id        VARCHAR(40)    NOT NULL PRIMARY KEY,
    supplier_code     VARCHAR(40)    NOT NULL,
    material_id       VARCHAR(40)    NOT NULL,
    agreed_price      NUMERIC(12,4)  NOT NULL,
    uom               VARCHAR(10)    NOT NULL,    -- KG / GM / LTR / PCS / BOX / BAG / ROLL / METER
    pack_size         NUMERIC(12,2),              -- e.g. 8000 (caps per box)
    pack_uom          VARCHAR(10),                -- BOX / BAG / ROLL / CARTON / BUNDLE / DRUM
    min_order_qty     NUMERIC(12,2)  NOT NULL DEFAULT 1,
    hsn_sac_code      VARCHAR(8)     NOT NULL,
    gst_rate          NUMERIC(5,2)   NOT NULL,    -- 0, 5, 12, 18, 28, etc.
    lead_time_days    INTEGER,                    -- nullable; falls back to supplier default
    currency_code     VARCHAR(3)     NOT NULL DEFAULT 'INR',
    effective_date    DATE           NOT NULL DEFAULT CURRENT_DATE,
    expiry_date       DATE,
    is_active         BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        VARCHAR(40)    NOT NULL,
    updated_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by        VARCHAR(40)    NOT NULL,

    CONSTRAINT uk_supplier_material UNIQUE (supplier_code, material_id),

    CONSTRAINT fk_smm_supplier FOREIGN KEY (supplier_code)
        REFERENCES erp_finance_schema.rm_supplier_tbl(supplier_code) ON DELETE CASCADE,

    -- NOTE: We intentionally do NOT add an FK to rm_material_tbl here, because
    -- the material module is going to be reworked next. Add the FK once the
    -- raw material rebuild lands. For now, application code enforces existence.

    CONSTRAINT chk_smm_uom CHECK (uom IN
        ('KG','GM','LTR','ML','PCS','BOX','BAG','ROLL','METER')),

    CONSTRAINT chk_smm_pack_uom CHECK (pack_uom IS NULL OR pack_uom IN
        ('BOX','BAG','ROLL','CARTON','BUNDLE','DRUM')),

    CONSTRAINT chk_smm_hsn CHECK (hsn_sac_code ~ '^[0-9]{4,8}$'),

    CONSTRAINT chk_smm_price CHECK (agreed_price >= 0),

    CONSTRAINT chk_smm_moq CHECK (min_order_qty >= 0),

    CONSTRAINT chk_smm_gst CHECK (gst_rate >= 0 AND gst_rate <= 100),

    CONSTRAINT chk_smm_lead_time CHECK (lead_time_days IS NULL OR lead_time_days >= 0),

    CONSTRAINT chk_smm_dates CHECK (expiry_date IS NULL OR expiry_date >= effective_date),

    -- If pack_size is set, pack_uom must be too, and vice versa
    CONSTRAINT chk_smm_pack_consistency CHECK
        ((pack_size IS NULL AND pack_uom IS NULL) OR
         (pack_size IS NOT NULL AND pack_uom IS NOT NULL))
);

CREATE INDEX idx_smm_supplier ON erp_finance_schema.rm_supplier_material_map_tbl (supplier_code);
CREATE INDEX idx_smm_material ON erp_finance_schema.rm_supplier_material_map_tbl (material_id);
CREATE INDEX idx_smm_active   ON erp_finance_schema.rm_supplier_material_map_tbl (is_active);

-- ============================================================================
-- 7. SEED DATA (matches the receipts you uploaded — useful for smoke-testing)
-- ============================================================================

-- Suppliers
INSERT INTO erp_finance_schema.rm_supplier_tbl
    (supplier_code, supplier_name, supplier_category, contact_person_name,
     gst_number, pan_number, state_code, payment_term, lead_time_days,
     created_by, updated_by)
VALUES
    ('SUPP-000001', 'U.S. Steriles',          'CHEMICALS', 'Makkala Sridhar',
     '27ADYPS0976L1ZF', 'ADYPS0976L', '27', 'ADVANCE_100', 7,  'Admin', 'Admin'),

    ('SUPP-000002', 'Nixa Branding Solutions','BRANDING',  NULL,
     '36AAQFN9168G1ZL', NULL,         '36', 'NET_30',     14, 'Admin', 'Admin'),

    ('SUPP-000003', 'SPPPL Compression Caps', 'CAPS',      NULL,
     NULL,             NULL,          '36', 'COD',        5,  'Admin', 'Admin');

-- Bump the sequence past the seeded codes so the next generated code is SUPP-000004
SELECT setval('erp_finance_schema.supplier_code_seq', 3, true);

-- Sample mapping rows (PO form will pick these up once the endpoints are live)
INSERT INTO erp_finance_schema.rm_supplier_material_map_tbl
    (mapping_id, supplier_code, material_id, agreed_price, uom,
     pack_size, pack_uom, min_order_qty, hsn_sac_code, gst_rate,
     created_by, updated_by)
VALUES
    -- U.S. Steriles - Potassium Bicarbonate (Food Grade), ₹257/kg
    ('MAP-000001', 'SUPP-000001', 'MAT-PB-FG',  257.00, 'KG',
     30,   'BAG', 30,  '28369990', 18, 'Admin', 'Admin'),

    -- Nixa Branding - PANDA BOPP Label 1000ML, ₹552/PCS in lots of 250
    ('MAP-000002', 'SUPP-000002', 'MAT-LBL-1L', 552.00, 'PCS',
     NULL, NULL,  250, '482110',   18, 'Admin', 'Admin'),

    -- SPPPL - Compression Caps, ₹0.33/PCS, 8000 per box
    ('MAP-000003', 'SUPP-000003', 'MAT-CAP-28', 0.33,   'PCS',
     8000, 'BOX', 8000, '39235010', 18, 'Admin', 'Admin');

-- ============================================================================
-- END
-- ============================================================================