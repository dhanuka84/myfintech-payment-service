-- Client table
CREATE TABLE client (
    id BIGINT PRIMARY KEY,
    client_name VARCHAR(255) NOT NULL,
    created_datetime TIMESTAMPTZ NOT NULL,
    updated_datetime TIMESTAMPTZ NOT NULL
);

-- Contract table
CREATE TABLE contract (
    id BIGINT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    contract_number VARCHAR(100) UNIQUE NOT NULL,
    created_datetime TIMESTAMPTZ NOT NULL,
    updated_datetime TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_contract_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

-- Payment table
CREATE TABLE payment (
    id BIGINT PRIMARY KEY,
    payment_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    type VARCHAR(20) CHECK (type IN ('CREDIT', 'DEBIT', 'TRANSFER','REFUND')) NOT NULL,
    contract_id BIGINT NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    created_datetime TIMESTAMPTZ NOT NULL,
    updated_datetime TIMESTAMPTZ NOT NULL,
    tracking_id BIGINT NOT NULL DEFAULT -1,
    CONSTRAINT fk_payment_contract FOREIGN KEY (contract_id) REFERENCES contract(id) ON DELETE CASCADE
    CONSTRAINT fk_payment_tracking FOREIGN KEY (tracking_id) REFERENCES payment_tracking(id) ON DELETE CASCADE
);

-- Payment Track table
CREATE TABLE payment_tracking (
    id BIGINT PRIMARY KEY,
    tracking_number VARCHAR(100) UNIQUE NOT NULL,
    created_datetime TIMESTAMPTZ NOT NULL,
    updated_datetime TIMESTAMPTZ NOT NULL
);

