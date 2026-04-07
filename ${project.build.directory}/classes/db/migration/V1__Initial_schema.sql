-- V1__Initial_schema.sql

-- Tenant table
CREATE TABLE tenant (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255) UNIQUE NOT NULL,
    plan VARCHAR(50),
    primary_color VARCHAR(7),
    app_name VARCHAR(255),
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User table
CREATE TABLE "user" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    region VARCHAR(100),
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, email)
);

-- User session table
CREATE TABLE user_session (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    user_id UUID NOT NULL REFERENCES "user"(id),
    refresh_token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dealer table
CREATE TABLE dealer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    owner_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'active',
    division VARCHAR(100),
    district VARCHAR(100),
    thana VARCHAR(100),
    territory VARCHAR(100),
    credit_limit DECIMAL(15,2),
    outstanding_balance DECIMAL(15,2) DEFAULT 0,
    total_sales DECIMAL(15,2) DEFAULT 0,
    joined_at DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, code)
);

-- Retailer table
CREATE TABLE retailer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    dealer_id UUID REFERENCES dealer(id),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    owner_name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    tier VARCHAR(20),
    business_type VARCHAR(100),
    outstanding_balance DECIMAL(15,2) DEFAULT 0,
    total_purchases DECIMAL(15,2) DEFAULT 0,
    visit_count INTEGER DEFAULT 0,
    order_count INTEGER DEFAULT 0,
    region VARCHAR(100),
    gps VARCHAR(100),
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, code)
);

-- Salesman table
CREATE TABLE salesman (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    user_id UUID REFERENCES "user"(id),
    region VARCHAR(100),
    division VARCHAR(100),
    district VARCHAR(100),
    manager_id UUID REFERENCES "user"(id),
    monthly_target DECIMAL(15,2),
    monthly_achieved DECIMAL(15,2) DEFAULT 0,
    achievement_pct DECIMAL(5,2) DEFAULT 0,
    visit_target INTEGER,
    visits_completed INTEGER DEFAULT 0,
    orders_this_month INTEGER DEFAULT 0,
    collection_this_month DECIMAL(15,2) DEFAULT 0,
    attendance_rate DECIMAL(5,2) DEFAULT 0,
    current_status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Attendance record table
CREATE TABLE attendance_record (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    salesman_id UUID NOT NULL REFERENCES salesman(id),
    date DATE NOT NULL,
    check_in_at TIMESTAMP,
    check_out_at TIMESTAMP,
    check_in_gps VARCHAR(100),
    check_out_gps VARCHAR(100),
    visits_planned INTEGER,
    visits_completed INTEGER DEFAULT 0,
    orders_placed INTEGER DEFAULT 0,
    collection_amount DECIMAL(15,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'present',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, salesman_id, date)
);

-- Retailer visit table
CREATE TABLE retailer_visit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    retailer_id UUID NOT NULL REFERENCES retailer(id),
    salesman_id UUID NOT NULL REFERENCES salesman(id),
    visited_at TIMESTAMP NOT NULL,
    duration_minutes INTEGER,
    outcome VARCHAR(50),
    order_amount DECIMAL(15,2),
    gps VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Product table
CREATE TABLE product (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    sku_code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    unit VARCHAR(20),
    base_price DECIMAL(10,2),
    trade_price DECIMAL(10,2),
    mrp DECIMAL(10,2),
    discount_pct DECIMAL(5,2) DEFAULT 0,
    stock_qty INTEGER DEFAULT 0,
    reorder_level INTEGER DEFAULT 0,
    sales_this_month INTEGER DEFAULT 0,
    sales_total INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, sku_code)
);

-- Order table
CREATE TABLE "order" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    order_number VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'submitted',
    salesman_id UUID REFERENCES salesman(id),
    retailer_id UUID NOT NULL REFERENCES retailer(id),
    dealer_id UUID REFERENCES dealer(id),
    subtotal DECIMAL(15,2),
    discount DECIMAL(15,2) DEFAULT 0,
    total DECIMAL(15,2),
    approved_by UUID REFERENCES "user"(id),
    approved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, order_number)
);

-- Order item table
CREATE TABLE order_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    order_id UUID NOT NULL REFERENCES "order"(id),
    sku_id UUID NOT NULL REFERENCES product(id),
    sku_code VARCHAR(50),
    product_name VARCHAR(255),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2),
    discount DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment table
CREATE TABLE payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    payment_number VARCHAR(50) NOT NULL,
    retailer_id UUID NOT NULL REFERENCES retailer(id),
    dealer_id UUID REFERENCES dealer(id),
    salesman_id UUID REFERENCES salesman(id),
    amount DECIMAL(15,2) NOT NULL,
    method VARCHAR(20),
    status VARCHAR(20) DEFAULT 'pending',
    received_at TIMESTAMP,
    verified_at TIMESTAMP,
    verified_by UUID REFERENCES "user"(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, payment_number)
);

-- Cheque details table
CREATE TABLE cheque_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    payment_id UUID NOT NULL REFERENCES payment(id),
    cheque_number VARCHAR(50) NOT NULL,
    bank_name VARCHAR(255),
    branch_name VARCHAR(255),
    cheque_date DATE,
    status VARCHAR(20) DEFAULT 'deposited',
    bounce_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ledger entry table
CREATE TABLE ledger_entry (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    entity_id UUID NOT NULL,
    entity_type VARCHAR(20) NOT NULL, -- dealer or retailer
    date DATE NOT NULL,
    description TEXT,
    type VARCHAR(10) NOT NULL, -- debit or credit
    amount DECIMAL(15,2) NOT NULL,
    balance DECIMAL(15,2),
    reference_type VARCHAR(20),
    reference_number VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Aging bucket table
CREATE TABLE aging_bucket (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    label VARCHAR(50) NOT NULL,
    min_days INTEGER,
    max_days INTEGER,
    amount DECIMAL(15,2) DEFAULT 0,
    dealer_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tenant config table
CREATE TABLE tenant_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    modules JSONB,
    regions JSONB,
    branding JSONB,
    workflows JSONB,
    geo_location JSONB,
    custom_fields JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id)
);

-- Managed user table (for user management)
CREATE TABLE managed_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    user_id UUID REFERENCES "user"(id),
    name VARCHAR(255),
    email VARCHAR(255),
    role VARCHAR(50),
    status VARCHAR(20),
    region VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit log table
CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    user_id UUID REFERENCES "user"(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    details JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notification table
CREATE TABLE notification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    user_id UUID REFERENCES "user"(id),
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50),
    priority VARCHAR(20) DEFAULT 'normal',
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sales prediction table
CREATE TABLE sales_prediction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    period VARCHAR(20) NOT NULL,
    actual DECIMAL(15,2),
    predicted DECIMAL(15,2),
    lower_bound DECIMAL(15,2),
    upper_bound DECIMAL(15,2),
    confidence DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Churn risk dealer table
CREATE TABLE churn_risk_dealer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    dealer_id UUID NOT NULL REFERENCES dealer(id),
    risk_score DECIMAL(5,2),
    risk_level VARCHAR(20),
    decline_indicators JSONB,
    recommendation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Demand forecast table
CREATE TABLE demand_forecast (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    product_id UUID NOT NULL REFERENCES product(id),
    period VARCHAR(20),
    forecasted_qty INTEGER,
    stockout_risk DECIMAL(5,2),
    confidence DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Anomaly alert table
CREATE TABLE anomaly_alert (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(20),
    status VARCHAR(20) DEFAULT 'open',
    resolved_at TIMESTAMP,
    resolved_by UUID REFERENCES "user"(id),
    details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_user_tenant_email ON "user"(tenant_id, email);
CREATE INDEX idx_dealer_tenant_code ON dealer(tenant_id, code);
CREATE INDEX idx_retailer_tenant_code ON retailer(tenant_id, code);
CREATE INDEX idx_order_tenant_number ON "order"(tenant_id, order_number);
CREATE INDEX idx_payment_tenant_number ON payment(tenant_id, payment_number);
CREATE INDEX idx_product_tenant_sku ON product(tenant_id, sku_code);
CREATE INDEX idx_attendance_tenant_salesman_date ON attendance_record(tenant_id, salesman_id, date);
CREATE INDEX idx_ledger_tenant_entity ON ledger_entry(tenant_id, entity_id, entity_type);
CREATE INDEX idx_audit_tenant_created ON audit_log(tenant_id, created_at);
CREATE INDEX idx_notification_tenant_user ON notification(tenant_id, user_id);