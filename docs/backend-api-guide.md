# DistribuNet Backend API Guide

## Purpose

This document is the backend handoff guide for building the DistribuNet server in Spring Boot with Spring Security, role-based access control, tenant isolation, and REST APIs that match the current frontend.

Use this file as the main reference when designing:

- database schema
- Spring Boot modules
- Spring Security roles and permissions
- request and response DTOs
- endpoint naming
- approval workflows
- audit logging
- tenant-aware authorization

It is based on the current frontend code and mock domain model in this repository.

## Product Summary

DistribuNet is a multi-tenant distribution management platform for managing the commercial chain:

- tenant
- users
- dealers
- retailers
- salesmen
- products
- orders
- payments
- ledgers
- reports
- AI insights
- notifications
- configuration

The business is modeled as a Bangladesh-oriented distribution workflow where dealers serve retailers, salesmen visit retailers and collect orders/payments, managers approve operational actions, and tenant admins manage platform settings.

## Backend Goals

The Spring Boot backend should provide:

- stateless secure APIs
- JWT-based authentication
- tenant isolation
- role and permission enforcement
- normalized core operational data
- approval workflow support
- auditability
- predictable REST response contracts
- pagination, filtering, and sorting support
- compatibility with current frontend entities

## Recommended Tech Stack

Suggested stack:

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway or Liquibase
- Validation with Jakarta Bean Validation
- JWT authentication
- MapStruct or manual mapping for DTOs
- Springdoc OpenAPI
- Redis optional for cache/session blacklist
- Quartz or Spring Scheduler optional for jobs

## Recommended Package Structure

Suggested package layout:

```text
com.distribnet
  ├─ common
  │  ├─ api
  │  ├─ config
  │  ├─ exception
  │  ├─ security
  │  ├─ tenant
  │  ├─ audit
  │  └─ util
  ├─ auth
  ├─ user
  ├─ dealer
  ├─ retailer
  ├─ salesman
  ├─ product
  ├─ order
  ├─ payment
  ├─ ledger
  ├─ dashboard
  ├─ report
  ├─ notification
  ├─ configuration
  └─ ai
```

Per module, keep a consistent structure:

```text
module/
  ├─ controller
  ├─ service
  ├─ repository
  ├─ domain
  ├─ dto
  ├─ mapper
  └─ specification
```

## Core Architectural Rules

### 1. Multi-Tenant First

Every business row must belong to a tenant unless it is true platform-wide master data.

Recommended pattern:

- `tenant` table for tenant metadata
- `tenant_id` foreign key on all tenant-owned records
- tenant extracted from JWT
- all queries automatically scoped by tenant

Never trust tenant id from request payload for authorization. Use authenticated tenant context.

### 2. Role And Permission Based Access

Support both:

- role-level access
- permission-level action checks

Recommended model:

- roles for broad user type
- permissions for specific actions
- endpoint access via `@PreAuthorize`
- service-layer checks for sensitive operations

### 3. Auditability

Create audit logs for:

- login/logout
- user create/update/suspend
- dealer create/update/deactivate
- order approval/rejection/status changes
- payment verification/rejection
- config changes

### 4. Consistent API Envelope

Use a consistent response format matching the frontend types.

Recommended response envelope:

```json
{
  "success": true,
  "data": {},
  "message": "Request successful",
  "errors": [],
  "meta": {
    "total": 100,
    "page": 1,
    "limit": 20,
    "hasNext": true,
    "hasPrev": false
  }
}
```

Error shape:

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "code": "VALIDATION_ERROR",
      "field": "email",
      "message": "Email is invalid"
    }
  ]
}
```

## Security Design

## Authentication Flow

Recommended auth flow:

1. User submits email and password.
2. Backend validates credentials.
3. Backend resolves tenant by email domain or user record.
4. Backend issues access token and refresh token.
5. Frontend stores access token and session info.
6. Protected endpoints require bearer token.

Recommended endpoints:

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`

### JWT Claims

Recommended JWT claims:

- `sub`: user id
- `tenantId`
- `email`
- `role`
- `permissions`
- `sessionId`

### Password Policy

Recommended rules:

- min 8 or 10 chars
- bcrypt password hash
- force reset optional
- account lock on repeated failed attempts optional

## Roles

Frontend-defined roles:

- `SUPER_ADMIN`
- `TENANT_ADMIN`
- `MANAGER`
- `SALESMAN`
- `DEALER`
- `RETAILER`

Recommended backend handling:

- `SUPER_ADMIN` for platform-wide administration
- `TENANT_ADMIN` for tenant administration
- `MANAGER` for approval and supervision
- `SALESMAN` for field execution
- `DEALER` and `RETAILER` only if dealer/retailer portal APIs are planned

If dealer and retailer do not log in in phase 1, keep the roles but do not expose full self-service APIs yet.

## Permissions

Recommended permission naming:

- `dashboard.read`
- `dealer.create`
- `dealer.read`
- `dealer.update`
- `dealer.deactivate`
- `retailer.create`
- `retailer.read`
- `retailer.update`
- `salesman.create`
- `salesman.read`
- `salesman.update`
- `attendance.read`
- `order.create`
- `order.read`
- `order.update`
- `order.approve`
- `order.reject`
- `payment.create`
- `payment.read`
- `payment.verify`
- `payment.reject`
- `product.create`
- `product.read`
- `product.update`
- `report.read`
- `ai.read`
- `config.read`
- `config.update`
- `user.create`
- `user.read`
- `user.update`
- `user.suspend`
- `notification.read`
- `notification.update`

### Suggested Role-Permission Matrix

`SUPER_ADMIN`

- full access across tenants

`TENANT_ADMIN`

- full tenant access except platform-only actions

`MANAGER`

- read all operational modules in tenant
- approve/reject orders
- verify/reject payments
- manage dealers, retailers, salesmen if business allows
- read reports and AI insights

`SALESMAN`

- read own dashboard scope
- read own retailers
- create orders
- create payments
- read own attendance and performance

`DEALER`

- read own orders, payments, ledger, profile if dealer portal exists

`RETAILER`

- read own orders, payments, profile if retailer portal exists

## Database Design Overview

## Core Tables

### tenant

- `id`
- `name`
- `domain`
- `plan`
- `primary_color`
- `app_name`
- `status`
- `created_at`
- `updated_at`

### user

- `id`
- `tenant_id`
- `name`
- `email`
- `phone`
- `password_hash`
- `role`
- `status`
- `region`
- `last_login_at`
- `created_at`
- `updated_at`

### user_session

- `id`
- `tenant_id`
- `user_id`
- `refresh_token_hash`
- `expires_at`
- `revoked_at`
- `created_at`

### dealer

- `id`
- `tenant_id`
- `code`
- `name`
- `owner_name`
- `email`
- `phone`
- `status`
- `division`
- `district`
- `thana`
- `territory`
- `credit_limit`
- `outstanding_balance`
- `total_sales`
- `joined_at`
- `created_at`
- `updated_at`

### retailer

- `id`
- `tenant_id`
- `dealer_id`
- `code`
- `name`
- `owner_name`
- `phone`
- `email`
- `address`
- `tier`
- `business_type`
- `status`
- `outstanding_balance`
- `total_purchases`
- `gps_lat`
- `gps_lng`
- `division`
- `district`
- `thana`
- `territory`
- `created_at`
- `updated_at`

### salesman

- `id`
- `tenant_id`
- `user_id`
- `manager_id`
- `name`
- `email`
- `phone`
- `status`
- `division`
- `district`
- `region`
- `monthly_target`
- `current_status`
- `joined_at`
- `created_at`
- `updated_at`

### attendance

- `id`
- `tenant_id`
- `salesman_id`
- `date`
- `check_in_at`
- `check_out_at`
- `check_in_lat`
- `check_in_lng`
- `check_out_lat`
- `check_out_lng`
- `visits_planned`
- `visits_completed`
- `orders_placed`
- `collection_amount`
- `status`

### retailer_visit

- `id`
- `tenant_id`
- `retailer_id`
- `salesman_id`
- `visited_at`
- `duration_minutes`
- `outcome`
- `order_id`
- `order_amount`
- `notes`
- `gps_lat`
- `gps_lng`

### product

- `id`
- `tenant_id`
- `sku_code`
- `name`
- `category`
- `sub_category`
- `unit`
- `base_price`
- `trade_price`
- `mrp`
- `discount_pct`
- `stock_qty`
- `reorder_level`
- `status`
- `barcode`
- `description`
- `created_at`
- `updated_at`

### order_header

- `id`
- `tenant_id`
- `order_number`
- `status`
- `salesman_id`
- `retailer_id`
- `dealer_id`
- `subtotal`
- `discount`
- `total`
- `notes`
- `approved_by`
- `approved_at`
- `created_at`
- `updated_at`

### order_item

- `id`
- `order_id`
- `sku_id`
- `sku_code`
- `product_name`
- `quantity`
- `unit`
- `unit_price`
- `discount`
- `total`

### payment

- `id`
- `tenant_id`
- `payment_number`
- `retailer_id`
- `dealer_id`
- `salesman_id`
- `amount`
- `method`
- `status`
- `notes`
- `received_at`
- `verified_at`
- `verified_by`
- `created_at`
- `updated_at`

### cheque_detail

- `id`
- `payment_id`
- `cheque_number`
- `bank_name`
- `branch_name`
- `cheque_date`
- `status`
- `deposited_at`
- `cleared_at`
- `bounced_at`
- `bounce_reason`

### ledger_entry

- `id`
- `tenant_id`
- `entity_id`
- `entity_type`
- `date`
- `description`
- `type`
- `amount`
- `balance`
- `reference_type`
- `reference_id`
- `reference_number`

### notification

- `id`
- `tenant_id`
- `user_id`
- `type`
- `severity`
- `title`
- `body`
- `read`
- `action_url`
- `action_label`
- `entity_type`
- `entity_id`
- `created_at`

### notification_preference

- `id`
- `tenant_id`
- `user_id`
- `type`
- `email_enabled`
- `push_enabled`
- `sms_enabled`

### tenant_config

- `id`
- `tenant_id`
- `json_payload`
- `updated_at`

### audit_log

- `id`
- `tenant_id`
- `user_id`
- `user_name`
- `action`
- `entity`
- `entity_id`
- `description`
- `ip`
- `user_agent`
- `created_at`

## Shared Enums

Use enums matching frontend values whenever possible.

### OrderStatus

- `DRAFT`
- `SUBMITTED`
- `APPROVED`
- `PROCESSING`
- `DISPATCHED`
- `DELIVERED`
- `COMPLETED`
- `CANCELLED`
- `RETURNED`

API JSON values should remain lowercase to match frontend:

- `draft`
- `submitted`
- `approved`
- `processing`
- `dispatched`
- `delivered`
- `completed`
- `cancelled`
- `returned`

### PaymentMethod

- `cash`
- `cheque`
- `bank_transfer`
- `mobile_banking`

### PaymentStatus

- `pending`
- `verified`
- `rejected`

### ChequeStatus

- `deposited`
- `cleared`
- `bounced`

### UserStatus

- `active`
- `inactive`
- `suspended`

### DealerStatus

- `active`
- `inactive`
- `on_hold`

### RetailerTier

- `A`
- `B`
- `C`

## API Versioning

Recommended base path:

- `/api/v1`

Suggested module paths:

- `/api/v1/auth`
- `/api/v1/dashboard`
- `/api/v1/dealers`
- `/api/v1/retailers`
- `/api/v1/salesmen`
- `/api/v1/orders`
- `/api/v1/payments`
- `/api/v1/products`
- `/api/v1/reports`
- `/api/v1/ai`
- `/api/v1/configuration`
- `/api/v1/users`
- `/api/v1/notifications`

## Shared API Rules

### Pagination

For list APIs support:

- `page`
- `limit`
- `search`
- `sortBy`
- `sortOrder`

List response:

```json
{
  "success": true,
  "data": {
    "items": [],
    "meta": {
      "total": 0,
      "page": 1,
      "limit": 20,
      "hasNext": false,
      "hasPrev": false
    }
  },
  "message": "Fetched successfully"
}
```

If you want to stay fully aligned to the frontend type definitions, keep:

- `data.items`
- `data.meta`

or return the frontend-shaped payload directly as `data`.

### Filtering

Use query params, for example:

`GET /api/v1/orders?page=1&limit=20&status=submitted&dealerId=123&search=ORD-`

### Sorting

Example:

`GET /api/v1/dealers?sortBy=createdAt&sortOrder=desc`

### Dates

Use ISO 8601 UTC timestamps for datetime fields.

Use `yyyy-MM-dd` only for pure date fields like cheque date when needed.

## Module API Design

## 1. Auth APIs

### POST `/api/v1/auth/login`

Request:

```json
{
  "email": "arif.hossain@meghnagroup.com",
  "password": "password123"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token",
    "user": {
      "id": "user-001",
      "tenantId": "tenant-abc-123",
      "name": "Arif Hossain",
      "email": "arif.hossain@meghnagroup.com",
      "phone": "+8801712345678",
      "role": "TENANT_ADMIN",
      "status": "active",
      "region": "Dhaka",
      "lastLoginAt": "2026-04-05T00:00:00Z",
      "createdAt": "2024-01-15T00:00:00Z",
      "updatedAt": "2026-04-05T00:00:00Z"
    },
    "tenant": {
      "id": "tenant-abc-123",
      "name": "Meghna Group FMCG",
      "domain": "meghnagroup.com",
      "plan": "enterprise",
      "primaryColor": "#1A3C5E",
      "appName": "DistribuNet"
    }
  },
  "message": "Login successful"
}
```

### POST `/api/v1/auth/refresh`

Request:

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

### POST `/api/v1/auth/logout`

Invalidate refresh token/session.

### GET `/api/v1/auth/me`

Returns current authenticated session user and tenant.

## 2. Dashboard APIs

These power the executive dashboard.

### GET `/api/v1/dashboard/kpis`

Response:

```json
{
  "success": true,
  "data": {
    "totalRevenue": 15200000,
    "revenueGrowth": 12.5,
    "activeDealers": 84,
    "dealersGrowth": 4.2,
    "orderVolume": 12430,
    "orderGrowth": 8.4,
    "collectionRate": 92,
    "collectionRateChange": 1.8,
    "activeSalesmen": 138,
    "salesmenGrowth": 3.5,
    "pendingOrders": 87,
    "overduePayments": 31
  },
  "message": "Dashboard KPIs fetched"
}
```

### GET `/api/v1/dashboard/revenue-trend`

Returns monthly trend list.

### GET `/api/v1/dashboard/top-salesmen?limit=6`

### GET `/api/v1/dashboard/recent-orders?limit=8`

### GET `/api/v1/dashboard/ai-insights`

### GET `/api/v1/dashboard/regional-performance`

### GET `/api/v1/dashboard/order-status-breakdown`

## 3. Dealer APIs

### GET `/api/v1/dealers`

Query params:

- `page`
- `limit`
- `search`
- `status`
- `division`
- `district`

Response item shape should match frontend `Dealer`.

### GET `/api/v1/dealers/{id}`

### POST `/api/v1/dealers`

Request:

```json
{
  "name": "Dhaka Metro Traders",
  "ownerName": "Md. Kamal Hossain",
  "email": "owner@metrotraders.com",
  "phone": "+8801712345678",
  "creditLimit": 2000000,
  "region": {
    "country": "BD",
    "division": "Dhaka",
    "district": "Dhaka"
  }
}
```

### PUT `/api/v1/dealers/{id}`

### PATCH `/api/v1/dealers/{id}/status`

Request:

```json
{
  "status": "inactive"
}
```

### Optional extra APIs

- `GET /api/v1/dealers/{id}/retailers`
- `GET /api/v1/dealers/{id}/ledger`
- `GET /api/v1/dealers/{id}/kpis`

## 4. Retailer APIs

### GET `/api/v1/retailers`

Query params:

- `page`
- `limit`
- `search`
- `status`
- `tier`
- `dealerId`
- `division`

### GET `/api/v1/retailers/{id}`

### POST `/api/v1/retailers`

Request:

```json
{
  "name": "Bismillah Store",
  "ownerName": "Abdul Karim",
  "phone": "+8801812345678",
  "email": "store@retail.example",
  "address": "Mirpur, Dhaka",
  "dealerId": "dealer-001",
  "tier": "A",
  "businessType": "General Trade",
  "region": {
    "division": "Dhaka",
    "district": "Dhaka",
    "thana": "Mirpur",
    "territory": "Mirpur-01"
  },
  "gps": {
    "lat": 23.8103,
    "lng": 90.4125
  }
}
```

### PUT `/api/v1/retailers/{id}`

### GET `/api/v1/retailers/{id}/visits`

### POST `/api/v1/retailers/{id}/visits`

Use this if visits are recorded by backend/mobile client.

## 5. Salesman APIs

### GET `/api/v1/salesmen`

Query params:

- `page`
- `limit`
- `search`
- `status`
- `division`
- `managerId`

### GET `/api/v1/salesmen/{id}`

### POST `/api/v1/salesmen`

Request:

```json
{
  "userId": "user-101",
  "managerId": "user-201",
  "division": "Dhaka",
  "district": "Dhaka",
  "region": "Dhaka North",
  "monthlyTarget": 500000,
  "status": "active"
}
```

### PUT `/api/v1/salesmen/{id}`

### GET `/api/v1/salesmen/{id}/attendance`

### POST `/api/v1/salesmen/{id}/attendance/check-in`

Request:

```json
{
  "date": "2026-04-05",
  "gps": {
    "lat": 23.8103,
    "lng": 90.4125
  }
}
```

### POST `/api/v1/salesmen/{id}/attendance/check-out`

### GET `/api/v1/salesmen/{id}/performance`

Optional aggregate KPI endpoint.

## 6. Product APIs

### GET `/api/v1/products`

Query params:

- `search`
- `category`
- `status`

### GET `/api/v1/products/{id}`

### POST `/api/v1/products`

Request:

```json
{
  "skuCode": "BISK-001",
  "name": "Olympic Biscuits Assorted",
  "category": "Food & Snacks",
  "subCategory": "Biscuits",
  "unit": "Pack",
  "basePrice": 30,
  "tradePrice": 28,
  "mrp": 35,
  "discountPct": 5,
  "stockQty": 1000,
  "reorderLevel": 200,
  "status": "active"
}
```

### PUT `/api/v1/products/{id}`

### PATCH `/api/v1/products/{id}/stock`

For controlled stock adjustments.

## 7. Order APIs

### GET `/api/v1/orders`

Query params:

- `page`
- `limit`
- `search`
- `status`
- `dealerId`
- `salesmanId`
- `retailerId`
- `from`
- `to`

### GET `/api/v1/orders/{id}`

### POST `/api/v1/orders`

Request:

```json
{
  "retailerId": "retailer-001",
  "items": [
    {
      "skuId": "product-001",
      "quantity": 20,
      "discount": 5
    }
  ],
  "notes": "Urgent delivery"
}
```

Backend responsibilities:

- resolve retailer and dealer
- resolve product prices
- calculate subtotal, discount, total
- generate `orderNumber`
- default status to `submitted` or `draft` based on workflow

### PUT `/api/v1/orders/{id}`

Allowed only while editable.

### POST `/api/v1/orders/{id}/approve`

Allowed to manager or tenant admin with permission.

Response should include updated order.

### POST `/api/v1/orders/{id}/reject`

Request:

```json
{
  "reason": "Credit limit exceeded"
}
```

### POST `/api/v1/orders/{id}/status`

Use for controlled transitions beyond approval.

Request:

```json
{
  "status": "processing"
}
```

### Suggested order transition rules

- `draft -> submitted`
- `submitted -> approved`
- `submitted -> cancelled`
- `approved -> processing`
- `processing -> dispatched`
- `dispatched -> delivered`
- `delivered -> completed`
- `completed -> returned` only by controlled flow if business requires

## 8. Payment APIs

### GET `/api/v1/payments`

Query params:

- `page`
- `limit`
- `search`
- `status`
- `method`
- `dealerId`
- `salesmanId`
- `from`
- `to`

### GET `/api/v1/payments/{id}`

### POST `/api/v1/payments`

Request:

```json
{
  "retailerId": "retailer-001",
  "amount": 50000,
  "method": "cheque",
  "receivedAt": "2026-04-05",
  "notes": "April collection",
  "chequeDetails": {
    "chequeNumber": "CQ-123456",
    "bankName": "Dutch-Bangla Bank",
    "branchName": "Mirpur",
    "chequeDate": "2026-04-10"
  }
}
```

Backend responsibilities:

- resolve retailer/dealer/salesman context
- create payment number
- set status `pending`
- create cheque detail if needed
- optionally create ledger impact after verification only

### POST `/api/v1/payments/{id}/verify`

### POST `/api/v1/payments/{id}/reject`

### POST `/api/v1/payments/{id}/cheque-status`

Request:

```json
{
  "status": "bounced",
  "bounceReason": "Insufficient funds"
}
```

### GET `/api/v1/payments/ledger/{entityId}`

Better alternative:

- `GET /api/v1/retailers/{id}/ledger`
- `GET /api/v1/dealers/{id}/ledger`

### GET `/api/v1/payments/aging-report`

Response should match aging bucket list used by frontend.

## 9. Report APIs

Reports should aggregate transactional tables, not duplicate business records.

### GET `/api/v1/reports/revenue-trend`

Params:

- `period`
- `region`

### GET `/api/v1/reports/regional-performance`

### GET `/api/v1/reports/order-status-breakdown`

### GET `/api/v1/reports/salesmen-performance`

### GET `/api/v1/reports/dealer-performance`

### GET `/api/v1/reports/export`

Recommended:

- support CSV
- async job for large exports if needed later

## 10. AI Insight APIs

These may initially read from derived tables or precomputed jobs.

### GET `/api/v1/ai/predictions`

### GET `/api/v1/ai/churn-risks`

### GET `/api/v1/ai/demand-forecasts`

### GET `/api/v1/ai/anomalies`

### POST `/api/v1/ai/anomalies/{id}/resolve`

For phase 1, these can still be database-backed rule engines rather than full ML.

## 11. Configuration APIs

### GET `/api/v1/configuration`

Returns `TenantConfig`.

### PUT `/api/v1/configuration`

Request should accept the full tenant config object or controlled sub-sections.

Recommended section endpoints too:

- `PUT /api/v1/configuration/modules`
- `PUT /api/v1/configuration/workflows`
- `PUT /api/v1/configuration/branding`
- `PUT /api/v1/configuration/custom-fields`

### Geo configuration

If geo data is tenant-customizable:

- `GET /api/v1/configuration/geo`
- `PUT /api/v1/configuration/geo`

If Bangladesh master geo data is global:

- `GET /api/v1/master-data/regions`
- `GET /api/v1/master-data/areas?regionId=...`
- `GET /api/v1/master-data/territories?areaId=...`

## 12. User Management APIs

### GET `/api/v1/users`

Query params:

- `search`
- `role`
- `status`

### GET `/api/v1/users/{id}`

### POST `/api/v1/users`

Request:

```json
{
  "name": "Md. Rafiqul Islam",
  "email": "rafiq@company.com",
  "phone": "+8801712345678",
  "role": "SALESMAN",
  "region": "Dhaka"
}
```

### PUT `/api/v1/users/{id}`

### POST `/api/v1/users/{id}/suspend`

### POST `/api/v1/users/{id}/reactivate`

### GET `/api/v1/users/audit-logs`

Optional better split:

- `GET /api/v1/audit-logs`

with filters:

- `entity`
- `action`
- `userId`
- `from`
- `to`

## 13. Notification APIs

### GET `/api/v1/notifications`

Query params:

- `unreadOnly`
- `type`
- `page`
- `limit`

### POST `/api/v1/notifications/{id}/read`

### POST `/api/v1/notifications/read-all`

### DELETE `/api/v1/notifications/{id}`

### GET `/api/v1/notifications/preferences`

### PUT `/api/v1/notifications/preferences`

Request:

```json
[
  {
    "type": "payment",
    "label": "Payment Alerts",
    "email": true,
    "push": true,
    "sms": true
  }
]
```

## DTO Design Recommendations

## Request DTO Principles

- request DTOs should be minimal and writable
- do not expose derived fields as writable
- validate with annotations
- do not accept `tenantId` from external client for tenant-owned records

Example:

- `CreateOrderRequest` should accept `retailerId`, `items`, `notes`
- it should not accept `dealerName`, `salesmanName`, `subtotal`, `total`

## Response DTO Principles

- response DTOs should be frontend-friendly
- include denormalized names where useful
- keep JSON field names aligned with frontend current types

Example:

- include both `dealerId` and `dealerName`
- include both `salesmanId` and `salesmanName`

## Validation Recommendations

Use Bean Validation:

- `@NotBlank`
- `@Email`
- `@Size`
- `@NotNull`
- `@Positive`
- `@PositiveOrZero`
- custom validators for tenant/domain rules if needed

Examples:

- corporate domain email validation for login or user creation
- order must contain at least one item
- cheque details required when payment method is cheque

## Service Layer Rules

Critical business logic should live in services, not controllers.

Examples:

- order total calculation
- order approval transition checks
- dealer credit validations
- payment verification and ledger posting
- tenant isolation enforcement
- notification creation
- audit log generation

## Important Business Rules To Implement

## Tenant Rules

- user may access only data from their tenant
- super admin may cross tenants only if explicitly allowed

## Order Rules

- salesman can create order only for allowed retailer scope
- submitted orders may require manager approval depending on tenant config
- only authorized roles may approve or reject
- status transitions must be controlled

## Payment Rules

- payment can be recorded against valid retailer only
- verification must be separate from recording if workflow requires approval
- bounced cheque must update cheque status and optionally trigger notification

## Dealer Rules

- deactivation should usually be soft delete
- credit exposure can affect order approval rules

## Notification Rules

- create notifications for approval-required events
- create notifications for bounced cheques and anomalies
- preference logic can control delivery channels later

## Audit Rules

- every sensitive mutation should create audit log
- store acting user, action, target entity, and metadata

## Suggested Spring Security Design

## Security Components

Recommended classes:

- `SecurityConfig`
- `JwtAuthenticationFilter`
- `JwtService`
- `CustomUserDetailsService`
- `CurrentUser` or `AuthenticatedUserPrincipal`
- `TenantContext`
- `PermissionEvaluator` or permission utility

## Authorization Approach

Use method security:

```java
@PreAuthorize("hasAuthority('order.approve')")
```

For tenant-scoped access to a single record, also validate in service:

```java
if (!order.getTenantId().equals(currentTenantId)) {
    throw new AccessDeniedException("Forbidden");
}
```

## Suggested OpenAPI Documentation

Document every endpoint with:

- summary
- description
- request schema
- response schema
- auth requirement
- required permission
- example payload

This project especially benefits from OpenAPI because the frontend already has strong DTO expectations.

## Development Priority Plan

Recommended implementation order:

1. Auth + security foundation
2. Tenant, user, role, permission model
3. Dealer APIs
4. Retailer APIs
5. Product APIs
6. Salesman + attendance APIs
7. Order APIs + approval flow
8. Payment + ledger + aging APIs
9. Dashboard/report aggregation APIs
10. Configuration APIs
11. Notifications + audit log APIs
12. AI insight APIs

## Frontend Compatibility Notes

To reduce frontend refactor work:

- keep enum values the same as current TypeScript types
- keep response field names aligned with `src/lib/types`
- support the current query/filter patterns
- return denormalized names in detail/list responses where frontend expects them

Important frontend entity references currently used:

- `src/lib/types/user.types.ts`
- `src/lib/types/dealer.types.ts`
- `src/lib/types/retailer-salesman.types.ts`
- `src/lib/types/order.types.ts`
- `src/lib/types/payment.types.ts`
- `src/lib/types/modules2.types.ts`
- `src/lib/types/api.types.ts`

## Final Recommendation

Build the backend as a tenant-aware modular monolith first.

That is the best fit because:

- the domain is broad but tightly connected
- transactions matter across orders, payments, ledgers, and audit logs
- the current frontend expects a unified API
- Spring Boot handles this model very well

Start with clean modules, strong DTO boundaries, and strict tenant-aware security. Once the transactional core is stable, you can later split AI/reporting or notification delivery into separate services if needed.
