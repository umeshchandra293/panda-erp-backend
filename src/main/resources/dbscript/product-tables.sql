CREATE TABLE IF NOT EXISTS "rm_material_schema".product_categories_tbl (
	category_id			VARCHAR(40) NOT NULL PRIMARY KEY,
	parent_id           VARCHAR(40),
	name                VARCHAR(100) NOT NULL,
	description         VARCHAR(255) NOT NULL,
    created_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     		VARCHAR(40) NOT NULL,
    updated_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by     		VARCHAR(40) NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES "rm_material_schema".product_categories_tbl(category_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".product_tbl (
	product_id			VARCHAR(40) NOT NULL PRIMARY KEY,
	sku                 VARCHAR(40) NOT NULL,
	name                VARCHAR(255) NOT NULL,
	category_id         VARCHAR(40) NOT NULL,
	base_cost           DECIMAL(19,4) NOT NULL,
	uom                 VARCHAR(20) DEFAULT 'Units',
	is_active           BOOLEAN DEFAULT TRUE,
    created_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     		VARCHAR(40) NOT NULL,
    updated_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by     		VARCHAR(40) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES "rm_material_schema".product_categories_tbl(category_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".product_base_price_tbl (
	base_price_id       VARCHAR(40) NOT NULL PRIMARY KEY,
	product_id          VARCHAR(40) NOT NULL,
	base_unit_price     DECIMAL(19,4) NOT NULL,
	currency_code 		CHAR(3) DEFAULT 'RS',
	effective_date		DATE NOT NULL DEFAULT CURRENT_DATE,
	end_date			DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     		VARCHAR(40) NOT NULL,
    updated_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by     		VARCHAR(40) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES "rm_material_schema".product_tbl(product_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "rm_material_schema".product_discount_tbl (
    discount_id			VARCHAR(40) NOT NULL PRIMARY KEY,
    product_id          VARCHAR(40) NOT NULL,
    name 				VARCHAR(100),
    discount_type 		VARCHAR(20), -- 'PERCENTAGE', 'FIXED_AMOUNT'
    value 				DECIMAL(19,4) NOT NULL,
    min_quantity 		INT DEFAULT 1, -- For volume-based discounts
	effective_date		DATE NOT NULL DEFAULT CURRENT_DATE,
	end_date			DATE NOT NULL DEFAULT CURRENT_DATE,    
    created_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     		VARCHAR(40) NOT NULL,
    updated_at     		TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by     		VARCHAR(40) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES "rm_material_schema".product_tbl(product_id) ON DELETE CASCADE
);
