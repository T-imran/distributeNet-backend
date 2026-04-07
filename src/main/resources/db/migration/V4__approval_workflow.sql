CREATE TABLE approval_request (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    requested_by_user_id UUID REFERENCES "user"(id),
    reviewed_by_user_id UUID REFERENCES "user"(id),
    source_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    summary VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    review_note VARCHAR(500),
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO permission_definition (code, name, module_name, description)
VALUES
    ('approval.read', 'View Approval Queue', 'approvals', 'View pending and processed approval requests'),
    ('approval.approve', 'Approve Requests', 'approvals', 'Approve pending admin or customer requests'),
    ('approval.reject', 'Reject Requests', 'approvals', 'Reject pending admin or customer requests')
ON CONFLICT (code) DO NOTHING;

CREATE INDEX idx_approval_request_tenant_source_status
    ON approval_request(tenant_id, source_type, status, created_at);
