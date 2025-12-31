-- 1. Foundation Tables
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id),
    role VARCHAR(255) CHECK (role IN ('ROLE_ORGANIZER','ROLE_ATTENDEE','ROLE_STAFF','ROLE_ADMIN')),
    UNIQUE (user_id, role)
);

-- 2. Auth & Events
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    token VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE events (
    id UUID PRIMARY KEY,
    organizer_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    venue VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL CHECK (status IN ('DRAFT','PUBLISHED','CANCELLED','COMPLETED')),
    start_time TIMESTAMPTZ,
    end_time TIMESTAMPTZ,
    sales_start TIMESTAMPTZ,
    sales_end TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT check_event_dates CHECK (end_time > start_time),
    CONSTRAINT check_sales_dates CHECK (sales_end > sales_start)
);

-- 3. Inventory & Staffing
CREATE TABLE ticket_types (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL REFERENCES events(id),
    name VARCHAR(255) NOT NULL,
    price NUMERIC(38,2) NOT NULL,
    total_quantity INTEGER NOT NULL,
    remaining_quantity INTEGER NOT NULL CHECK (remaining_quantity >= 0),
    version BIGINT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    UNIQUE (event_id, name)
);

CREATE TABLE event_staff (
    event_id UUID NOT NULL REFERENCES events(id),
    user_id UUID NOT NULL REFERENCES users(id),
    PRIMARY KEY (user_id, event_id)
);

-- 4. Transactions & Validation
CREATE TABLE tickets (
    id UUID PRIMARY KEY,
    purchaser_id UUID NOT NULL REFERENCES users(id),
    ticket_type_id UUID NOT NULL REFERENCES ticket_types(id),
    status VARCHAR(255) NOT NULL CHECK (status IN ('PURCHASED','CANCELLED','USED')),
    version BIGINT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE qr_codes (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL REFERENCES tickets(id),
    value VARCHAR(2048) NOT NULL,
    status VARCHAR(255) NOT NULL CHECK (status IN ('ACTIVE','EXPIRED','REVOKED')),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE ticket_validation (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL REFERENCES tickets(id),
    validated_by UUID NOT NULL REFERENCES users(id),
    status VARCHAR(255) NOT NULL CHECK (status IN ('VALID','INVALID','EXPIRED')),
    validation_method VARCHAR(255) NOT NULL CHECK (validation_method IN ('QR_SCAN','MANUAL')),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

-- 5. Strategic Indexes
CREATE INDEX idx_events_start_time ON events (start_time);
CREATE INDEX idx_events_status ON events (status);
CREATE INDEX idx_qr_value ON qr_codes (value);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens (user_id);