CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_address_tbl (
    address_id       VARCHAR(40) NOT NULL PRIMARY KEY,
    address_line_1   VARCHAR(100) NOT NULL,
    address_line_2   VARCHAR(100),
    po_box_number    VARCHAR(20),
    city             VARCHAR(60) NOT NULL,
    state_cd         VARCHAR(40) NOT NULL,
    postal_cd        VARCHAR(20) NOT NULL,
    country_cd       VARCHAR(20) NOT NULL,
    time_zone        VARCHAR(20),
    is_primary       BOOLEAN DEFAULT FALSE,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40) NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_phone_tbl (
    phone_id         VARCHAR(40) NOT NULL PRIMARY KEY,
    phone_type       VARCHAR(10) NOT NULL,
    phone_number     VARCHAR(20) NOT NULL,
    phone_extension  VARCHAR(6),
    is_primary       BOOLEAN DEFAULT FALSE,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40) NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40) NOT NULL,
    UNIQUE(phone_number)
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".rm_email_tbl (
    email_id         VARCHAR(40) NOT NULL PRIMARY KEY,
    email            VARCHAR(255) NOT NULL UNIQUE,
    is_primary       BOOLEAN DEFAULT FALSE,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(40) NOT NULL,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by       VARCHAR(40) NOT NULL
);