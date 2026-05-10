-- =========================
-- User management (authz)
-- =========================

-- Users are application accounts (can optionally link to an employee record).
CREATE TABLE app_user (
  user_id         BIGSERIAL PRIMARY KEY,
  company_id      BIGINT REFERENCES company(company_id) ON DELETE CASCADE, -- optional if multi-tenant; keep if you need tenancy
  employee_id     BIGINT UNIQUE REFERENCES employee(employee_id) ON DELETE SET NULL,

  username        VARCHAR(80) NOT NULL,
  email           VARCHAR(254),
  password_hash   TEXT NOT NULL,            -- store a hash (bcrypt/argon2), never plaintext
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,

  last_login_at   TIMESTAMPTZ,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

  UNIQUE (company_id, username),
  UNIQUE (company_id, email)
);

-- Role-based access control (RBAC)
CREATE TABLE role (
  role_id         BIGSERIAL PRIMARY KEY,
  company_id      BIGINT REFERENCES company(company_id) ON DELETE CASCADE, -- tenant-scoped roles (or NULL for global)
  name            VARCHAR(100) NOT NULL,
  description     TEXT,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (company_id, name)
);

CREATE TABLE permission (
  permission_id   BIGSERIAL PRIMARY KEY,
  code            VARCHAR(150) NOT NULL,    -- e.g., "employee.read", "employee.write"
  description     TEXT,
  UNIQUE (code)
);

-- Many-to-many: users <-> roles
CREATE TABLE user_role (
  user_id         BIGINT NOT NULL REFERENCES app_user(user_id) ON DELETE CASCADE,
  role_id         BIGINT NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, role_id)
);

-- Many-to-many: roles <-> permissions
CREATE TABLE role_permission (
  role_id         BIGINT NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
  permission_id   BIGINT NOT NULL REFERENCES permission(permission_id) ON DELETE CASCADE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (role_id, permission_id)
);

-- Optional: audit log for admin/security visibility
CREATE TABLE audit_log (
  audit_log_id    BIGSERIAL PRIMARY KEY,
  company_id      BIGINT REFERENCES company(company_id) ON DELETE CASCADE,
  actor_user_id   BIGINT REFERENCES app_user(user_id) ON DELETE SET NULL,
  action          VARCHAR(120) NOT NULL,   -- e.g., "user.create", "employee.update"
  entity_type     VARCHAR(120),            -- e.g., "employee"
  entity_id       VARCHAR(120),            -- store as text to support multiple PK types
  details         JSONB,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
