<<<<<<< HEAD
-- ============================================================================
-- HS ERP - SALES MODULE TABLES
-- Run AFTER supplier, material, and PO tables are in place.
-- ============================================================================

SET search_path TO erp_finance_schema;

-- ── Drop existing (clean slate) ───────────────────────────────────────────
DROP TABLE IF EXISTS erp_finance_schema.sales_payment_tbl          CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_order_item_tbl       CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_order_tbl            CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_visit_tbl            CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_daily_target_tbl     CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_retailer_pricing_tbl CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_product_tbl          CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_retailer_tbl         CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_salesman_tbl         CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_route_tbl            CASCADE;

DROP SEQUENCE IF EXISTS erp_finance_schema.sales_route_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_salesman_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_retailer_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_product_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_target_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_visit_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_order_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_order_item_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_payment_seq;

-- ── Sequences ─────────────────────────────────────────────────────────────
CREATE SEQUENCE erp_finance_schema.sales_route_seq      START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_salesman_seq   START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_retailer_seq   START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_product_seq    START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_target_seq     START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_visit_seq      START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_order_seq      START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_order_item_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_payment_seq    START 1 INCREMENT 1 NO MAXVALUE CACHE 1;

-- ── 1. Routes ─────────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_route_tbl (
    route_id    VARCHAR(40)  NOT NULL PRIMARY KEY,
    route_name  VARCHAR(100) NOT NULL,
    area_name   VARCHAR(100) NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40)  NOT NULL DEFAULT 'Admin',
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40)  NOT NULL DEFAULT 'Admin'
);

-- ── 2. Salesmen ───────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_salesman_tbl (
    salesman_id VARCHAR(40)  NOT NULL PRIMARY KEY,
    username    VARCHAR(40)  NOT NULL UNIQUE,  -- matches auth store username
    full_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(15),
    route_id    VARCHAR(40),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40)  NOT NULL DEFAULT 'Admin',
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40)  NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_salesman_route
        FOREIGN KEY (route_id)
        REFERENCES erp_finance_schema.sales_route_tbl(route_id)
        ON DELETE SET NULL
);

-- ── 3. Retailers ──────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_retailer_tbl (
    retailer_id          VARCHAR(40)    NOT NULL PRIMARY KEY,
    shop_name            VARCHAR(200)   NOT NULL,
    owner_name           VARCHAR(100)   NOT NULL,
    phone                VARCHAR(15)    NOT NULL,
    address              VARCHAR(500),
    area                 VARCHAR(100),
    gps_lat              NUMERIC(10,7),
    gps_lng              NUMERIC(10,7),
    assigned_salesman_id VARCHAR(40),
    credit_limit         NUMERIC(12,2)  NOT NULL DEFAULT 5000.00,
    current_balance      NUMERIC(12,2)  NOT NULL DEFAULT 0.00,
    is_active            BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by           VARCHAR(40)    NOT NULL DEFAULT 'Admin',
    updated_at           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by           VARCHAR(40)    NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_retailer_salesman
        FOREIGN KEY (assigned_salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE SET NULL,

    CONSTRAINT chk_credit_limit    CHECK (credit_limit >= 0),
    CONSTRAINT chk_current_balance CHECK (current_balance >= 0)
);

CREATE INDEX idx_retailer_salesman ON erp_finance_schema.sales_retailer_tbl (assigned_salesman_id);
CREATE INDEX idx_retailer_area     ON erp_finance_schema.sales_retailer_tbl (area);

-- ── 4. Products ───────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_product_tbl (
    product_id   VARCHAR(40)    NOT NULL PRIMARY KEY,
    product_name VARCHAR(200)   NOT NULL,
    sku          VARCHAR(50)    NOT NULL UNIQUE,
    base_price   NUMERIC(10,2)  NOT NULL,
    unit         VARCHAR(20)    NOT NULL DEFAULT 'PCS',
    is_active    BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(40)    NOT NULL DEFAULT 'Admin',
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by   VARCHAR(40)    NOT NULL DEFAULT 'Admin',

    CONSTRAINT chk_product_base_price CHECK (base_price >= 0)
);

-- ── 5. Retailer-specific pricing ──────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_retailer_pricing_tbl (
    retailer_id    VARCHAR(40)   NOT NULL,
    product_id     VARCHAR(40)   NOT NULL,
    custom_price   NUMERIC(10,2) NOT NULL,
    effective_from DATE          NOT NULL DEFAULT CURRENT_DATE,
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     VARCHAR(40)   NOT NULL DEFAULT 'Admin',
    updated_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by     VARCHAR(40)   NOT NULL DEFAULT 'Admin',

    PRIMARY KEY (retailer_id, product_id),

    CONSTRAINT fk_pricing_retailer
        FOREIGN KEY (retailer_id)
        REFERENCES erp_finance_schema.sales_retailer_tbl(retailer_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pricing_product
        FOREIGN KEY (product_id)
        REFERENCES erp_finance_schema.sales_product_tbl(product_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_custom_price CHECK (custom_price >= 0)
);

-- ── 6. Daily targets ──────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_daily_target_tbl (
    target_id          VARCHAR(40) NOT NULL PRIMARY KEY,
    salesman_id        VARCHAR(40) NOT NULL,
    target_date        DATE        NOT NULL,
    visit_target       INTEGER     NOT NULL DEFAULT 0,
    order_target       INTEGER     NOT NULL DEFAULT 0,
    collection_target  NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by         VARCHAR(40) NOT NULL DEFAULT 'Admin',
    updated_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by         VARCHAR(40) NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_target_salesman
        FOREIGN KEY (salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_target_salesman_date UNIQUE (salesman_id, target_date)
);

CREATE INDEX idx_target_salesman ON erp_finance_schema.sales_daily_target_tbl (salesman_id);
CREATE INDEX idx_target_date     ON erp_finance_schema.sales_daily_target_tbl (target_date);

-- ── 7. Visits ─────────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_visit_tbl (
    visit_id      VARCHAR(40)  NOT NULL PRIMARY KEY,
    salesman_id   VARCHAR(40)  NOT NULL,
    retailer_id   VARCHAR(40)  NOT NULL,
    visit_date    DATE         NOT NULL DEFAULT CURRENT_DATE,
    check_in_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gps_lat       NUMERIC(10,7),
    gps_lng       NUMERIC(10,7),
    gps_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    remarks       VARCHAR(1000),
    status        VARCHAR(20)  NOT NULL DEFAULT 'VISITED',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(40)  NOT NULL DEFAULT 'Admin',
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(40)  NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_visit_salesman
        FOREIGN KEY (salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_visit_retailer
        FOREIGN KEY (retailer_id)
        REFERENCES erp_finance_schema.sales_retailer_tbl(retailer_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_visit_status CHECK (status IN (
        'VISITED', 'NO_CONTACT', 'CLOSED'
    ))
);

CREATE INDEX idx_visit_salesman ON erp_finance_schema.sales_visit_tbl (salesman_id);
CREATE INDEX idx_visit_retailer ON erp_finance_schema.sales_visit_tbl (retailer_id);
CREATE INDEX idx_visit_date     ON erp_finance_schema.sales_visit_tbl (visit_date);

-- ── 8. Orders ─────────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_order_tbl (
    order_id      VARCHAR(40)   NOT NULL PRIMARY KEY,
    salesman_id   VARCHAR(40)   NOT NULL,
    retailer_id   VARCHAR(40)   NOT NULL,
    visit_id      VARCHAR(40),                    -- nullable: order can exist without visit
    order_date    DATE          NOT NULL DEFAULT CURRENT_DATE,
    total_amount  NUMERIC(12,2) NOT NULL DEFAULT 0,
    status        VARCHAR(20)   NOT NULL DEFAULT 'PLACED',
    notes         VARCHAR(500),
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(40)   NOT NULL DEFAULT 'Admin',
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(40)   NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_order_salesman
        FOREIGN KEY (salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_order_retailer
        FOREIGN KEY (retailer_id)
        REFERENCES erp_finance_schema.sales_retailer_tbl(retailer_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_order_visit
        FOREIGN KEY (visit_id)
        REFERENCES erp_finance_schema.sales_visit_tbl(visit_id)
        ON DELETE SET NULL,

    CONSTRAINT chk_order_status CHECK (status IN (
        'PLACED', 'DISPATCHED', 'CANCELLED'
    )),

    CONSTRAINT chk_order_total CHECK (total_amount >= 0)
);

CREATE INDEX idx_order_salesman ON erp_finance_schema.sales_order_tbl (salesman_id);
CREATE INDEX idx_order_retailer ON erp_finance_schema.sales_order_tbl (retailer_id);
CREATE INDEX idx_order_date     ON erp_finance_schema.sales_order_tbl (order_date);

-- ── 9. Order items ────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_order_item_tbl (
    item_id     VARCHAR(40)   NOT NULL PRIMARY KEY,
    order_id    VARCHAR(40)   NOT NULL,
    product_id  VARCHAR(40)   NOT NULL,
    quantity    NUMERIC(10,3) NOT NULL,
    unit_price  NUMERIC(10,2) NOT NULL,
    line_total  NUMERIC(12,2) NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40)   NOT NULL DEFAULT 'Admin',
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40)   NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_item_order
        FOREIGN KEY (order_id)
        REFERENCES erp_finance_schema.sales_order_tbl(order_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_product
        FOREIGN KEY (product_id)
        REFERENCES erp_finance_schema.sales_product_tbl(product_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_item_qty        CHECK (quantity > 0),
    CONSTRAINT chk_item_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_item_line_total CHECK (line_total >= 0)
);

CREATE INDEX idx_item_order   ON erp_finance_schema.sales_order_item_tbl (order_id);
CREATE INDEX idx_item_product ON erp_finance_schema.sales_order_item_tbl (product_id);

-- ── 10. Payments ──────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_payment_tbl (
    payment_id       VARCHAR(40)   NOT NULL PRIMARY KEY,
    salesman_id      VARCHAR(40)   NOT NULL,
    retailer_id      VARCHAR(40)   NOT NULL,
    visit_id         VARCHAR(40),
    payment_date     DATE          NOT NULL DEFAULT CURRENT_DATE,
    amount           NUMERIC(12,2) NOT NULL,
    payment_mode     VARCHAR(10)   NOT NULL DEFAULT 'CASH',
    reference_number VARCHAR(100),
    notes            VARCHAR(500),
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40)   NOT NULL DEFAULT 'Admin',
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40)   NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_payment_salesman
        FOREIGN KEY (salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_payment_retailer
        FOREIGN KEY (retailer_id)
        REFERENCES erp_finance_schema.sales_retailer_tbl(retailer_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_payment_visit
        FOREIGN KEY (visit_id)
        REFERENCES erp_finance_schema.sales_visit_tbl(visit_id)
        ON DELETE SET NULL,

    CONSTRAINT chk_payment_mode CHECK (payment_mode IN ('CASH', 'UPI', 'CREDIT')),
    CONSTRAINT chk_payment_amount CHECK (amount > 0)
);

CREATE INDEX idx_payment_salesman ON erp_finance_schema.sales_payment_tbl (salesman_id);
CREATE INDEX idx_payment_retailer ON erp_finance_schema.sales_payment_tbl (retailer_id);
CREATE INDEX idx_payment_date     ON erp_finance_schema.sales_payment_tbl (payment_date);

-- ============================================================================
-- SEED DATA
-- ============================================================================

-- Routes
INSERT INTO erp_finance_schema.sales_route_tbl
    (route_id, route_name, area_name)
VALUES
    ('ROUTE-001', 'Route A', 'Banjara Hills'),
    ('ROUTE-002', 'Route B', 'Madhapur'),
    ('ROUTE-003', 'Route C', 'Kukatpally');

-- Salesmen (username matches auth.store mock users)
INSERT INTO erp_finance_schema.sales_salesman_tbl
    (salesman_id, username, full_name, phone, route_id)
VALUES
    ('SMAN-001', 'emp1042', 'Rahul Sharma',  '9876543210', 'ROUTE-001'),
    ('SMAN-002', 'emp1043', 'Vikram Singh',  '9123456789', 'ROUTE-002'),
    ('SMAN-003', 'emp1044', 'Amit Patel',    '9988776655', 'ROUTE-003');

-- Products (Panda Aqua product line)
INSERT INTO erp_finance_schema.sales_product_tbl
    (product_id, product_name, sku, base_price, unit)
VALUES
    ('PROD-001', '20L Panda Aqua Bubble',       'BUB-20L',  65.00, 'PCS'),
    ('PROD-002', '1L Panda Aqua (Box of 12)',    'BOX-1L',  120.00, 'BOX'),
    ('PROD-003', '500ml Panda Aqua (Box of 24)', 'BOX-500M',150.00, 'BOX'),
    ('PROD-004', '250ml Panda Aqua (Box of 48)', 'BOX-250M',180.00, 'BOX');

-- Retailers
INSERT INTO erp_finance_schema.sales_retailer_tbl
    (retailer_id, shop_name, owner_name, phone, address, area,
     gps_lat, gps_lng, assigned_salesman_id, credit_limit, current_balance)
VALUES
    ('RET-001', 'Ravi Stores',    'Ravi Kumar',  '9876543210',
     '12-3-456, Banjara Hills', 'Banjara Hills',
     17.4156, 78.4347, 'SMAN-001', 20000.00, 12000.00),

    ('RET-002', 'Kiran Mart',     'Kiran Reddy', '9123456789',
     '5-6-789, Jubilee Hills',  'Jubilee Hills',
     17.4239, 78.4085, 'SMAN-001', 15000.00, 0.00),

    ('RET-003', 'Balaji Kirana',  'Suresh Babu', '9988776655',
     '78-A, SR Nagar',          'SR Nagar',
     17.4475, 78.4460, 'SMAN-002', 10000.00, 4500.00),

    ('RET-004', 'Royal Traders',  'Abdul Karim', '9000000000',
     '23, Madhapur Main Road',  'Madhapur',
     17.4483, 78.3915, 'SMAN-002', 30000.00, 28000.00),

    ('RET-005', 'Sunrise Mart',   'Vijay Kumar', '9090909090',
     '101, KPHB Colony',        'Kukatpally',
     17.4940, 78.3900, 'SMAN-003', 25000.00, 5200.00);

-- Retailer custom pricing (Royal Traders gets a discount on 20L)
INSERT INTO erp_finance_schema.sales_retailer_pricing_tbl
    (retailer_id, product_id, custom_price)
VALUES
    ('RET-004', 'PROD-001', 60.00),   -- Royal Traders: 20L at ₹60 instead of ₹65
    ('RET-004', 'PROD-002', 110.00);  -- Royal Traders: 1L box at ₹110 instead of ₹120

-- Daily targets for today
INSERT INTO erp_finance_schema.sales_daily_target_tbl
    (target_id, salesman_id, target_date, visit_target, order_target, collection_target)
VALUES
    ('TGT-001', 'SMAN-001', CURRENT_DATE, 15, 8,  50000.00),
    ('TGT-002', 'SMAN-002', CURRENT_DATE, 20, 12, 80000.00),
    ('TGT-003', 'SMAN-003', CURRENT_DATE, 18, 10, 60000.00);

-- Sample visits (last 3 days)
INSERT INTO erp_finance_schema.sales_visit_tbl
    (visit_id, salesman_id, retailer_id, visit_date, gps_lat, gps_lng,
     gps_verified, remarks, status)
VALUES
    ('VIS-001', 'SMAN-001', 'RET-001', CURRENT_DATE - 2,
     17.4155, 78.4346, TRUE, 'Met owner. Discussed payment. Promised by Friday.', 'VISITED'),
    ('VIS-002', 'SMAN-001', 'RET-002', CURRENT_DATE - 2,
     17.4238, 78.4084, TRUE, 'Placed order for 1L boxes.', 'VISITED'),
    ('VIS-003', 'SMAN-002', 'RET-003', CURRENT_DATE - 1,
     17.4474, 78.4459, TRUE, 'Collected partial payment of ₹2000.', 'VISITED'),
    ('VIS-004', 'SMAN-002', 'RET-004', CURRENT_DATE - 1,
     17.4482, 78.3914, TRUE, 'Owner not available. Left note.', 'NO_CONTACT'),
    ('VIS-005', 'SMAN-001', 'RET-001', CURRENT_DATE,
     17.4156, 78.4347, TRUE, 'Collected ₹5000 cash payment.', 'VISITED');

-- Sample orders
INSERT INTO erp_finance_schema.sales_order_tbl
    (order_id, salesman_id, retailer_id, visit_id, order_date, total_amount, status)
VALUES
    ('ORD-001', 'SMAN-001', 'RET-002', 'VIS-002', CURRENT_DATE - 2, 1200.00, 'DISPATCHED'),
    ('ORD-002', 'SMAN-002', 'RET-003', 'VIS-003', CURRENT_DATE - 1,  325.00, 'PLACED');

INSERT INTO erp_finance_schema.sales_order_item_tbl
    (item_id, order_id, product_id, quantity, unit_price, line_total)
VALUES
    ('ITM-001', 'ORD-001', 'PROD-002', 10, 120.00, 1200.00),
    ('ITM-002', 'ORD-002', 'PROD-001',  5,  65.00,  325.00);

-- Sample payments
INSERT INTO erp_finance_schema.sales_payment_tbl
    (payment_id, salesman_id, retailer_id, visit_id,
     payment_date, amount, payment_mode, notes)
VALUES
    ('PAY-001', 'SMAN-002', 'RET-003', 'VIS-003',
     CURRENT_DATE - 1, 2000.00, 'CASH', 'Partial payment against outstanding balance'),
    ('PAY-002', 'SMAN-001', 'RET-001', 'VIS-005',
     CURRENT_DATE,     5000.00, 'CASH', 'Cash collected during morning visit');

-- Set sequences past seeded IDs
SELECT setval('erp_finance_schema.sales_route_seq',      3, true);
SELECT setval('erp_finance_schema.sales_salesman_seq',   3, true);
SELECT setval('erp_finance_schema.sales_retailer_seq',   5, true);
SELECT setval('erp_finance_schema.sales_product_seq',    4, true);
SELECT setval('erp_finance_schema.sales_target_seq',     3, true);
SELECT setval('erp_finance_schema.sales_visit_seq',      5, true);
SELECT setval('erp_finance_schema.sales_order_seq',      2, true);
SELECT setval('erp_finance_schema.sales_order_item_seq', 2, true);
SELECT setval('erp_finance_schema.sales_payment_seq',    2, true);

-- ── VERIFY ────────────────────────────────────────────────────────────────
SELECT 'routes'    AS tbl, COUNT(*) FROM erp_finance_schema.sales_route_tbl    UNION ALL
SELECT 'salesmen'  AS tbl, COUNT(*) FROM erp_finance_schema.sales_salesman_tbl UNION ALL
SELECT 'retailers' AS tbl, COUNT(*) FROM erp_finance_schema.sales_retailer_tbl UNION ALL
SELECT 'products'  AS tbl, COUNT(*) FROM erp_finance_schema.sales_product_tbl  UNION ALL
SELECT 'targets'   AS tbl, COUNT(*) FROM erp_finance_schema.sales_daily_target_tbl UNION ALL
SELECT 'visits'    AS tbl, COUNT(*) FROM erp_finance_schema.sales_visit_tbl    UNION ALL
SELECT 'orders'    AS tbl, COUNT(*) FROM erp_finance_schema.sales_order_tbl    UNION ALL
=======
-- ============================================================================
-- HS ERP - SALES MODULE TABLES
-- Run AFTER supplier, material, and PO tables are in place.
-- ============================================================================

SET search_path TO erp_finance_schema;

-- ── Drop existing (clean slate) ───────────────────────────────────────────
DROP TABLE IF EXISTS erp_finance_schema.sales_payment_tbl          CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_order_item_tbl       CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_order_tbl            CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_visit_tbl            CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_daily_target_tbl     CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_retailer_pricing_tbl CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_product_tbl          CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_retailer_tbl         CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_salesman_tbl         CASCADE;
DROP TABLE IF EXISTS erp_finance_schema.sales_route_tbl            CASCADE;

DROP SEQUENCE IF EXISTS erp_finance_schema.sales_route_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_salesman_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_retailer_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_product_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_target_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_visit_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_order_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_order_item_seq;
DROP SEQUENCE IF EXISTS erp_finance_schema.sales_payment_seq;

-- ── Sequences ─────────────────────────────────────────────────────────────
CREATE SEQUENCE erp_finance_schema.sales_route_seq      START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_salesman_seq   START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_retailer_seq   START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_product_seq    START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_target_seq     START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_visit_seq      START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_order_seq      START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_order_item_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
CREATE SEQUENCE erp_finance_schema.sales_payment_seq    START 1 INCREMENT 1 NO MAXVALUE CACHE 1;

-- ── 1. Routes ─────────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_route_tbl (
    route_id    VARCHAR(40)  NOT NULL PRIMARY KEY,
    route_name  VARCHAR(100) NOT NULL,
    area_name   VARCHAR(100) NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40)  NOT NULL DEFAULT 'Admin',
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40)  NOT NULL DEFAULT 'Admin'
);

-- ── 2. Salesmen ───────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_salesman_tbl (
    salesman_id VARCHAR(40)  NOT NULL PRIMARY KEY,
    username    VARCHAR(40)  NOT NULL UNIQUE,  -- matches auth store username
    full_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(15),
    route_id    VARCHAR(40),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40)  NOT NULL DEFAULT 'Admin',
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40)  NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_salesman_route
        FOREIGN KEY (route_id)
        REFERENCES erp_finance_schema.sales_route_tbl(route_id)
        ON DELETE SET NULL
);

-- ── 3. Retailers ──────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_retailer_tbl (
    retailer_id          VARCHAR(40)    NOT NULL PRIMARY KEY,
    shop_name            VARCHAR(200)   NOT NULL,
    owner_name           VARCHAR(100)   NOT NULL,
    phone                VARCHAR(15)    NOT NULL,
    address              VARCHAR(500),
    area                 VARCHAR(100),
    gps_lat              NUMERIC(10,7),
    gps_lng              NUMERIC(10,7),
    assigned_salesman_id VARCHAR(40),
    credit_limit         NUMERIC(12,2)  NOT NULL DEFAULT 5000.00,
    current_balance      NUMERIC(12,2)  NOT NULL DEFAULT 0.00,
    is_active            BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by           VARCHAR(40)    NOT NULL DEFAULT 'Admin',
    updated_at           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by           VARCHAR(40)    NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_retailer_salesman
        FOREIGN KEY (assigned_salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE SET NULL,

    CONSTRAINT chk_credit_limit    CHECK (credit_limit >= 0),
    CONSTRAINT chk_current_balance CHECK (current_balance >= 0)
);

CREATE INDEX idx_retailer_salesman ON erp_finance_schema.sales_retailer_tbl (assigned_salesman_id);
CREATE INDEX idx_retailer_area     ON erp_finance_schema.sales_retailer_tbl (area);

-- ── 4. Products ───────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_product_tbl (
    product_id   VARCHAR(40)    NOT NULL PRIMARY KEY,
    product_name VARCHAR(200)   NOT NULL,
    sku          VARCHAR(50)    NOT NULL UNIQUE,
    base_price   NUMERIC(10,2)  NOT NULL,
    unit         VARCHAR(20)    NOT NULL DEFAULT 'PCS',
    is_active    BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(40)    NOT NULL DEFAULT 'Admin',
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by   VARCHAR(40)    NOT NULL DEFAULT 'Admin',

    CONSTRAINT chk_product_base_price CHECK (base_price >= 0)
);

-- ── 5. Retailer-specific pricing ──────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_retailer_pricing_tbl (
    retailer_id    VARCHAR(40)   NOT NULL,
    product_id     VARCHAR(40)   NOT NULL,
    custom_price   NUMERIC(10,2) NOT NULL,
    effective_from DATE          NOT NULL DEFAULT CURRENT_DATE,
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     VARCHAR(40)   NOT NULL DEFAULT 'Admin',
    updated_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by     VARCHAR(40)   NOT NULL DEFAULT 'Admin',

    PRIMARY KEY (retailer_id, product_id),

    CONSTRAINT fk_pricing_retailer
        FOREIGN KEY (retailer_id)
        REFERENCES erp_finance_schema.sales_retailer_tbl(retailer_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pricing_product
        FOREIGN KEY (product_id)
        REFERENCES erp_finance_schema.sales_product_tbl(product_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_custom_price CHECK (custom_price >= 0)
);

-- ── 6. Daily targets ──────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_daily_target_tbl (
    target_id          VARCHAR(40) NOT NULL PRIMARY KEY,
    salesman_id        VARCHAR(40) NOT NULL,
    target_date        DATE        NOT NULL,
    visit_target       INTEGER     NOT NULL DEFAULT 0,
    order_target       INTEGER     NOT NULL DEFAULT 0,
    collection_target  NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by         VARCHAR(40) NOT NULL DEFAULT 'Admin',
    updated_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by         VARCHAR(40) NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_target_salesman
        FOREIGN KEY (salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_target_salesman_date UNIQUE (salesman_id, target_date)
);

CREATE INDEX idx_target_salesman ON erp_finance_schema.sales_daily_target_tbl (salesman_id);
CREATE INDEX idx_target_date     ON erp_finance_schema.sales_daily_target_tbl (target_date);

-- ── 7. Visits ─────────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_visit_tbl (
    visit_id      VARCHAR(40)  NOT NULL PRIMARY KEY,
    salesman_id   VARCHAR(40)  NOT NULL,
    retailer_id   VARCHAR(40)  NOT NULL,
    visit_date    DATE         NOT NULL DEFAULT CURRENT_DATE,
    check_in_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gps_lat       NUMERIC(10,7),
    gps_lng       NUMERIC(10,7),
    gps_verified  BOOLEAN      NOT NULL DEFAULT FALSE,
    remarks       VARCHAR(1000),
    status        VARCHAR(20)  NOT NULL DEFAULT 'VISITED',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(40)  NOT NULL DEFAULT 'Admin',
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(40)  NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_visit_salesman
        FOREIGN KEY (salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_visit_retailer
        FOREIGN KEY (retailer_id)
        REFERENCES erp_finance_schema.sales_retailer_tbl(retailer_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_visit_status CHECK (status IN (
        'VISITED', 'NO_CONTACT', 'CLOSED'
    ))
);

CREATE INDEX idx_visit_salesman ON erp_finance_schema.sales_visit_tbl (salesman_id);
CREATE INDEX idx_visit_retailer ON erp_finance_schema.sales_visit_tbl (retailer_id);
CREATE INDEX idx_visit_date     ON erp_finance_schema.sales_visit_tbl (visit_date);

-- ── 8. Orders ─────────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_order_tbl (
    order_id      VARCHAR(40)   NOT NULL PRIMARY KEY,
    salesman_id   VARCHAR(40)   NOT NULL,
    retailer_id   VARCHAR(40)   NOT NULL,
    visit_id      VARCHAR(40),                    -- nullable: order can exist without visit
    order_date    DATE          NOT NULL DEFAULT CURRENT_DATE,
    total_amount  NUMERIC(12,2) NOT NULL DEFAULT 0,
    status        VARCHAR(20)   NOT NULL DEFAULT 'PLACED',
    notes         VARCHAR(500),
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(40)   NOT NULL DEFAULT 'Admin',
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by    VARCHAR(40)   NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_order_salesman
        FOREIGN KEY (salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_order_retailer
        FOREIGN KEY (retailer_id)
        REFERENCES erp_finance_schema.sales_retailer_tbl(retailer_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_order_visit
        FOREIGN KEY (visit_id)
        REFERENCES erp_finance_schema.sales_visit_tbl(visit_id)
        ON DELETE SET NULL,

    CONSTRAINT chk_order_status CHECK (status IN (
        'PLACED', 'DISPATCHED', 'CANCELLED'
    )),

    CONSTRAINT chk_order_total CHECK (total_amount >= 0)
);

CREATE INDEX idx_order_salesman ON erp_finance_schema.sales_order_tbl (salesman_id);
CREATE INDEX idx_order_retailer ON erp_finance_schema.sales_order_tbl (retailer_id);
CREATE INDEX idx_order_date     ON erp_finance_schema.sales_order_tbl (order_date);

-- ── 9. Order items ────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_order_item_tbl (
    item_id     VARCHAR(40)   NOT NULL PRIMARY KEY,
    order_id    VARCHAR(40)   NOT NULL,
    product_id  VARCHAR(40)   NOT NULL,
    quantity    NUMERIC(10,3) NOT NULL,
    unit_price  NUMERIC(10,2) NOT NULL,
    line_total  NUMERIC(12,2) NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(40)   NOT NULL DEFAULT 'Admin',
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by  VARCHAR(40)   NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_item_order
        FOREIGN KEY (order_id)
        REFERENCES erp_finance_schema.sales_order_tbl(order_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_product
        FOREIGN KEY (product_id)
        REFERENCES erp_finance_schema.sales_product_tbl(product_id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_item_qty        CHECK (quantity > 0),
    CONSTRAINT chk_item_unit_price CHECK (unit_price >= 0),
    CONSTRAINT chk_item_line_total CHECK (line_total >= 0)
);

CREATE INDEX idx_item_order   ON erp_finance_schema.sales_order_item_tbl (order_id);
CREATE INDEX idx_item_product ON erp_finance_schema.sales_order_item_tbl (product_id);

-- ── 10. Payments ──────────────────────────────────────────────────────────
CREATE TABLE erp_finance_schema.sales_payment_tbl (
    payment_id       VARCHAR(40)   NOT NULL PRIMARY KEY,
    salesman_id      VARCHAR(40)   NOT NULL,
    retailer_id      VARCHAR(40)   NOT NULL,
    visit_id         VARCHAR(40),
    payment_date     DATE          NOT NULL DEFAULT CURRENT_DATE,
    amount           NUMERIC(12,2) NOT NULL,
    payment_mode     VARCHAR(10)   NOT NULL DEFAULT 'CASH',
    reference_number VARCHAR(100),
    notes            VARCHAR(500),
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40)   NOT NULL DEFAULT 'Admin',
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40)   NOT NULL DEFAULT 'Admin',

    CONSTRAINT fk_payment_salesman
        FOREIGN KEY (salesman_id)
        REFERENCES erp_finance_schema.sales_salesman_tbl(salesman_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_payment_retailer
        FOREIGN KEY (retailer_id)
        REFERENCES erp_finance_schema.sales_retailer_tbl(retailer_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_payment_visit
        FOREIGN KEY (visit_id)
        REFERENCES erp_finance_schema.sales_visit_tbl(visit_id)
        ON DELETE SET NULL,

    CONSTRAINT chk_payment_mode CHECK (payment_mode IN ('CASH', 'UPI', 'CREDIT')),
    CONSTRAINT chk_payment_amount CHECK (amount > 0)
);

CREATE INDEX idx_payment_salesman ON erp_finance_schema.sales_payment_tbl (salesman_id);
CREATE INDEX idx_payment_retailer ON erp_finance_schema.sales_payment_tbl (retailer_id);
CREATE INDEX idx_payment_date     ON erp_finance_schema.sales_payment_tbl (payment_date);

-- ============================================================================
-- SEED DATA
-- ============================================================================

-- Routes
INSERT INTO erp_finance_schema.sales_route_tbl
    (route_id, route_name, area_name)
VALUES
    ('ROUTE-001', 'Route A', 'Banjara Hills'),
    ('ROUTE-002', 'Route B', 'Madhapur'),
    ('ROUTE-003', 'Route C', 'Kukatpally');

-- Salesmen (username matches auth.store mock users)
INSERT INTO erp_finance_schema.sales_salesman_tbl
    (salesman_id, username, full_name, phone, route_id)
VALUES
    ('SMAN-001', 'emp1042', 'Rahul Sharma',  '9876543210', 'ROUTE-001'),
    ('SMAN-002', 'emp1043', 'Vikram Singh',  '9123456789', 'ROUTE-002'),
    ('SMAN-003', 'emp1044', 'Amit Patel',    '9988776655', 'ROUTE-003');

-- Products (Panda Aqua product line)
INSERT INTO erp_finance_schema.sales_product_tbl
    (product_id, product_name, sku, base_price, unit)
VALUES
    ('PROD-001', '20L Panda Aqua Bubble',       'BUB-20L',  65.00, 'PCS'),
    ('PROD-002', '1L Panda Aqua (Box of 12)',    'BOX-1L',  120.00, 'BOX'),
    ('PROD-003', '500ml Panda Aqua (Box of 24)', 'BOX-500M',150.00, 'BOX'),
    ('PROD-004', '250ml Panda Aqua (Box of 48)', 'BOX-250M',180.00, 'BOX');

-- Retailers
INSERT INTO erp_finance_schema.sales_retailer_tbl
    (retailer_id, shop_name, owner_name, phone, address, area,
     gps_lat, gps_lng, assigned_salesman_id, credit_limit, current_balance)
VALUES
    ('RET-001', 'Ravi Stores',    'Ravi Kumar',  '9876543210',
     '12-3-456, Banjara Hills', 'Banjara Hills',
     17.4156, 78.4347, 'SMAN-001', 20000.00, 12000.00),

    ('RET-002', 'Kiran Mart',     'Kiran Reddy', '9123456789',
     '5-6-789, Jubilee Hills',  'Jubilee Hills',
     17.4239, 78.4085, 'SMAN-001', 15000.00, 0.00),

    ('RET-003', 'Balaji Kirana',  'Suresh Babu', '9988776655',
     '78-A, SR Nagar',          'SR Nagar',
     17.4475, 78.4460, 'SMAN-002', 10000.00, 4500.00),

    ('RET-004', 'Royal Traders',  'Abdul Karim', '9000000000',
     '23, Madhapur Main Road',  'Madhapur',
     17.4483, 78.3915, 'SMAN-002', 30000.00, 28000.00),

    ('RET-005', 'Sunrise Mart',   'Vijay Kumar', '9090909090',
     '101, KPHB Colony',        'Kukatpally',
     17.4940, 78.3900, 'SMAN-003', 25000.00, 5200.00);

-- Retailer custom pricing (Royal Traders gets a discount on 20L)
INSERT INTO erp_finance_schema.sales_retailer_pricing_tbl
    (retailer_id, product_id, custom_price)
VALUES
    ('RET-004', 'PROD-001', 60.00),   -- Royal Traders: 20L at ₹60 instead of ₹65
    ('RET-004', 'PROD-002', 110.00);  -- Royal Traders: 1L box at ₹110 instead of ₹120

-- Daily targets for today
INSERT INTO erp_finance_schema.sales_daily_target_tbl
    (target_id, salesman_id, target_date, visit_target, order_target, collection_target)
VALUES
    ('TGT-001', 'SMAN-001', CURRENT_DATE, 15, 8,  50000.00),
    ('TGT-002', 'SMAN-002', CURRENT_DATE, 20, 12, 80000.00),
    ('TGT-003', 'SMAN-003', CURRENT_DATE, 18, 10, 60000.00);

-- Sample visits (last 3 days)
INSERT INTO erp_finance_schema.sales_visit_tbl
    (visit_id, salesman_id, retailer_id, visit_date, gps_lat, gps_lng,
     gps_verified, remarks, status)
VALUES
    ('VIS-001', 'SMAN-001', 'RET-001', CURRENT_DATE - 2,
     17.4155, 78.4346, TRUE, 'Met owner. Discussed payment. Promised by Friday.', 'VISITED'),
    ('VIS-002', 'SMAN-001', 'RET-002', CURRENT_DATE - 2,
     17.4238, 78.4084, TRUE, 'Placed order for 1L boxes.', 'VISITED'),
    ('VIS-003', 'SMAN-002', 'RET-003', CURRENT_DATE - 1,
     17.4474, 78.4459, TRUE, 'Collected partial payment of ₹2000.', 'VISITED'),
    ('VIS-004', 'SMAN-002', 'RET-004', CURRENT_DATE - 1,
     17.4482, 78.3914, TRUE, 'Owner not available. Left note.', 'NO_CONTACT'),
    ('VIS-005', 'SMAN-001', 'RET-001', CURRENT_DATE,
     17.4156, 78.4347, TRUE, 'Collected ₹5000 cash payment.', 'VISITED');

-- Sample orders
INSERT INTO erp_finance_schema.sales_order_tbl
    (order_id, salesman_id, retailer_id, visit_id, order_date, total_amount, status)
VALUES
    ('ORD-001', 'SMAN-001', 'RET-002', 'VIS-002', CURRENT_DATE - 2, 1200.00, 'DISPATCHED'),
    ('ORD-002', 'SMAN-002', 'RET-003', 'VIS-003', CURRENT_DATE - 1,  325.00, 'PLACED');

INSERT INTO erp_finance_schema.sales_order_item_tbl
    (item_id, order_id, product_id, quantity, unit_price, line_total)
VALUES
    ('ITM-001', 'ORD-001', 'PROD-002', 10, 120.00, 1200.00),
    ('ITM-002', 'ORD-002', 'PROD-001',  5,  65.00,  325.00);

-- Sample payments
INSERT INTO erp_finance_schema.sales_payment_tbl
    (payment_id, salesman_id, retailer_id, visit_id,
     payment_date, amount, payment_mode, notes)
VALUES
    ('PAY-001', 'SMAN-002', 'RET-003', 'VIS-003',
     CURRENT_DATE - 1, 2000.00, 'CASH', 'Partial payment against outstanding balance'),
    ('PAY-002', 'SMAN-001', 'RET-001', 'VIS-005',
     CURRENT_DATE,     5000.00, 'CASH', 'Cash collected during morning visit');

-- Set sequences past seeded IDs
SELECT setval('erp_finance_schema.sales_route_seq',      3, true);
SELECT setval('erp_finance_schema.sales_salesman_seq',   3, true);
SELECT setval('erp_finance_schema.sales_retailer_seq',   5, true);
SELECT setval('erp_finance_schema.sales_product_seq',    4, true);
SELECT setval('erp_finance_schema.sales_target_seq',     3, true);
SELECT setval('erp_finance_schema.sales_visit_seq',      5, true);
SELECT setval('erp_finance_schema.sales_order_seq',      2, true);
SELECT setval('erp_finance_schema.sales_order_item_seq', 2, true);
SELECT setval('erp_finance_schema.sales_payment_seq',    2, true);

-- ── VERIFY ────────────────────────────────────────────────────────────────
SELECT 'routes'    AS tbl, COUNT(*) FROM erp_finance_schema.sales_route_tbl    UNION ALL
SELECT 'salesmen'  AS tbl, COUNT(*) FROM erp_finance_schema.sales_salesman_tbl UNION ALL
SELECT 'retailers' AS tbl, COUNT(*) FROM erp_finance_schema.sales_retailer_tbl UNION ALL
SELECT 'products'  AS tbl, COUNT(*) FROM erp_finance_schema.sales_product_tbl  UNION ALL
SELECT 'targets'   AS tbl, COUNT(*) FROM erp_finance_schema.sales_daily_target_tbl UNION ALL
SELECT 'visits'    AS tbl, COUNT(*) FROM erp_finance_schema.sales_visit_tbl    UNION ALL
SELECT 'orders'    AS tbl, COUNT(*) FROM erp_finance_schema.sales_order_tbl    UNION ALL
>>>>>>> e83a97dde2129e673c5e7be6d373df2ebbfc7df2
SELECT 'payments'  AS tbl, COUNT(*) FROM erp_finance_schema.sales_payment_tbl;