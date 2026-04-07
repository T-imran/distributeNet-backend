CREATE TABLE permission_definition (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    module_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role_definition (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenant(id),
    code VARCHAR(100) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    base_role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, code)
);

CREATE TABLE role_permission_assignment (
    role_id UUID NOT NULL REFERENCES role_definition(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permission_definition(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

ALTER TABLE "user"
    ADD COLUMN role_definition_id UUID REFERENCES role_definition(id);

INSERT INTO permission_definition (code, name, module_name, description)
VALUES
    ('user.read', 'View Users', 'user-management', 'View user records and assignments'),
    ('user.create', 'Create Users', 'user-management', 'Create new users from the admin panel'),
    ('user.update', 'Update Users', 'user-management', 'Update user profile and status'),
    ('user.assign-role', 'Assign User Roles', 'user-management', 'Assign a role to an existing user'),
    ('role.read', 'View Roles', 'user-management', 'View role definitions and permissions'),
    ('role.create', 'Create Roles', 'user-management', 'Create new custom roles'),
    ('role.update', 'Update Roles', 'user-management', 'Update role details and permission assignments'),
    ('permission.read', 'View Permissions', 'user-management', 'View the available permission catalog')
ON CONFLICT (code) DO NOTHING;

CREATE INDEX idx_role_definition_tenant_code ON role_definition(tenant_id, code);
CREATE INDEX idx_user_role_definition ON "user"(role_definition_id);
