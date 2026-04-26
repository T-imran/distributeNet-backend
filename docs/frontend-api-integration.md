# Frontend API Integration Guide

## Overview

This document is for integrating the frontend with the current Spring Boot backend in this repository.

Base URL:

```text
http://localhost:8080
```

API base path:

```text
/api/v1
```

Swagger endpoints:

```text
/swagger-ui.html
/api-docs
```

Current backend note:

- Most APIs require JWT authentication.
- Some endpoints return the shared `ApiResponse` envelope.
- Some older CRUD endpoints return raw DTO arrays/objects directly.
- There is no pagination implemented yet on list endpoints.
- Tenant scoping is taken from the authenticated user, not from the request body.

## Runtime And Auth

Current backend config:

- Port: `8080` by default
- Database: in-memory H2
- Auth: JWT access token + refresh token
- Public routes: `/api/v1/auth/**`
- Protected routes: everything else under `/api/v1/**`

Authorization header:

```http
Authorization: Bearer <accessToken>
```

## Standard Envelope

Many endpoints return:

```json
{
  "success": true,
  "data": {},
  "message": "Request successful",
  "errors": [],
  "meta": {}
}
```

Validation or business errors usually return:

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "code": "VALIDATION_ERROR",
      "field": "email",
      "message": "must be a well-formed email address"
    }
  ]
}
```

## Important Integration Warning

The API is currently mixed:

- `auth`, `dashboard`, `notifications`, `approvals`, `configuration`, and `/api/v1/admin/**` return `ApiResponse`
- `dealers`, `retailers`, `salesmen`, `products`, `orders`, `payments`, `users`, and `ai/insights` return raw JSON objects or arrays

Frontend API helpers should support both response styles.

Suggested rule:

- If response has `success`, use `response.data.data`
- Otherwise use `response.data` directly

## Suggested Frontend API Client

```ts
import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const refreshToken = localStorage.getItem("refreshToken");
      if (refreshToken) {
        try {
          const refreshResponse = await axios.post(
            "http://localhost:8080/api/v1/auth/refresh",
            { refreshToken }
          );

          const session = refreshResponse.data.data;
          localStorage.setItem("accessToken", session.accessToken);
          localStorage.setItem("refreshToken", session.refreshToken);

          error.config.headers.Authorization = `Bearer ${session.accessToken}`;
          return axios(error.config);
        } catch {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
        }
      }
    }

    return Promise.reject(error);
  }
);

export function unwrapApi<T>(response: any): T {
  if (response?.data?.success !== undefined) {
    return response.data.data as T;
  }
  return response.data as T;
}
```

## Authentication APIs

### `POST /auth/login`

Request:

```json
{
  "email": "admin@example.com",
  "password": "password123"
}
```

Response shape:

```json
{
  "success": true,
  "data": {
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token",
    "tokenType": "Bearer",
    "user": {
      "id": "uuid",
      "tenantId": "uuid",
      "name": "Admin User",
      "email": "admin@example.com",
      "phone": null,
      "role": "TENANT_ADMIN",
      "status": "active",
      "region": null,
      "lastLoginAt": "2026-04-26T04:00:00",
      "createdAt": "2026-04-25T12:00:00",
      "updatedAt": "2026-04-26T04:00:00"
    },
    "tenant": {
      "id": "uuid",
      "name": "example.com",
      "domain": "example.com",
      "plan": null,
      "primaryColor": null,
      "appName": null
    }
  },
  "message": "Login successful"
}
```

Use in frontend:

- Store `accessToken`
- Store `refreshToken`
- Store `user`
- Store `tenant`

### `POST /auth/register`

Request:

```json
{
  "name": "Admin User",
  "email": "admin@example.com",
  "password": "password123",
  "role": "TENANT_ADMIN",
  "tenantDomain": "example.com"
}
```

Response:

```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "email": "admin@example.com"
  },
  "message": "User registered successfully"
}
```

### `POST /auth/refresh`

Request:

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

Response:

- Same session structure as login

### `POST /auth/logout`

Request:

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

Response:

```json
{
  "success": true,
  "message": "Logout successful"
}
```

### `GET /auth/me`

Headers:

- `Authorization: Bearer <accessToken>`

Response:

- Same `AuthSessionDto` shape as login, but `accessToken` and `refreshToken` may be `null`

## Dashboard APIs

All dashboard endpoints return `ApiResponse`.

### `GET /dashboard/kpis`

Response `data`:

```json
{
  "tenantId": "uuid",
  "totalRevenue": 10000,
  "activeDealers": 12,
  "activeRetailers": 40,
  "activeSalesmen": 6,
  "orderVolume": 120,
  "pendingOrders": 10,
  "verifiedPayments": 8,
  "overduePayments": 1
}
```

### `GET /dashboard/recent-orders?limit=8`

Response `data`:

```json
[
  {
    "id": "uuid",
    "orderNumber": "ORD-001",
    "status": "submitted",
    "total": 3000,
    "createdAt": "2026-04-26T09:00:00"
  }
]
```

### `GET /dashboard/top-salesmen?limit=6`

Response `data`:

```json
[
  {
    "id": "uuid",
    "userId": "uuid",
    "region": "Dhaka",
    "monthlyAchieved": 50000,
    "monthlyTarget": 75000,
    "achievementPct": 66.67
  }
]
```

### `GET /dashboard/order-status-breakdown`

Response `data`:

```json
{
  "submitted": 10,
  "approved": 3,
  "processing": 2,
  "dispatched": 1,
  "delivered": 0,
  "completed": 8,
  "cancelled": 1,
  "returned": 0
}
```

## Dealer APIs

Dealer endpoints return raw DTOs, not `ApiResponse`.

Dealer DTO:

```json
{
  "id": "uuid",
  "code": "DLR-001",
  "name": "Dealer Name",
  "ownerName": "Owner Name",
  "email": "dealer@example.com",
  "phone": "01700000000",
  "status": "ACTIVE",
  "division": "Dhaka",
  "district": "Dhaka",
  "thana": "Mirpur",
  "territory": "North",
  "creditLimit": 100000,
  "outstandingBalance": 25000,
  "totalSales": 450000,
  "joinedAt": "2026-04-01"
}
```

Routes:

- `GET /dealers`
- `GET /dealers/{id}`
- `POST /dealers`
- `PUT /dealers/{id}`
- `DELETE /dealers/{id}`

Delete response is wrapped:

```json
{
  "success": true,
  "message": "Dealer deactivated"
}
```

Frontend note:

- `status` must match enum text like `ACTIVE`, `INACTIVE`, or `ON_HOLD`

## Retailer APIs

Retailer endpoints return raw DTOs except delete.

Retailer DTO:

```json
{
  "id": "uuid",
  "code": "RTL-001",
  "name": "Retailer Name",
  "ownerName": "Owner Name",
  "phone": "01700000000",
  "email": "retailer@example.com",
  "address": "Dhaka",
  "tier": "A",
  "businessType": "General Trade",
  "outstandingBalance": 10000,
  "totalPurchases": 200000,
  "visitCount": 5,
  "orderCount": 12,
  "region": "Dhaka North",
  "gps": "23.8103,90.4125",
  "status": "ACTIVE",
  "dealerId": "uuid"
}
```

Routes:

- `GET /retailers`
- `GET /retailers/{id}`
- `POST /retailers`
- `PUT /retailers/{id}`
- `DELETE /retailers/{id}`

Frontend note:

- `region` is a plain string, not an object
- `gps` is a plain string, not `{ lat, lng }`

## Salesman APIs

Salesman endpoints return raw DTOs except delete.

Salesman DTO:

```json
{
  "id": "uuid",
  "userId": null,
  "region": "Dhaka",
  "division": "Dhaka",
  "district": "Dhaka",
  "managerId": "uuid",
  "monthlyTarget": 100000,
  "monthlyAchieved": 45000,
  "achievementPct": 45,
  "visitTarget": 100,
  "visitsCompleted": 54,
  "ordersThisMonth": 20,
  "collectionThisMonth": 70000,
  "attendanceRate": 92,
  "currentStatus": "ACTIVE"
}
```

Routes:

- `GET /salesmen`
- `GET /salesmen/{id}`
- `POST /salesmen`
- `PUT /salesmen/{id}`
- `DELETE /salesmen/{id}`

Frontend note:

- Current service does not persist `userId` or `managerId` from create/update payloads
- Treat those links as unstable until backend is improved

## Product APIs

Product endpoints return raw DTOs except delete.

Product DTO:

```json
{
  "id": "uuid",
  "skuCode": "SKU-001",
  "name": "Product Name",
  "category": "Beverage",
  "unit": "pcs",
  "basePrice": 100,
  "tradePrice": 90,
  "mrp": 110,
  "discountPct": 5,
  "stockQty": 500,
  "reorderLevel": 100,
  "salesThisMonth": 60,
  "salesTotal": 5000,
  "status": "ACTIVE"
}
```

Routes:

- `GET /products`
- `GET /products/{id}`
- `POST /products`
- `PUT /products/{id}`
- `DELETE /products/{id}`

## Order APIs

Order endpoints return raw DTOs except delete.

Order DTO:

```json
{
  "id": "uuid",
  "orderNumber": "ORD-001",
  "status": "SUBMITTED",
  "salesmanId": "uuid",
  "retailerId": "uuid",
  "dealerId": "uuid",
  "subtotal": 1000,
  "discount": 50,
  "total": 950,
  "approvedBy": null,
  "items": [
    {
      "skuId": "uuid",
      "skuCode": "SKU-001",
      "productName": "Product Name",
      "quantity": 5,
      "unitPrice": 200,
      "discount": 0,
      "total": 1000
    }
  ]
}
```

Routes:

- `GET /orders`
- `GET /orders/{id}`
- `POST /orders`
- `POST /orders/{id}/approve`
- `DELETE /orders/{id}`

Frontend notes:

- Creation expects full totals from frontend right now
- Backend does not calculate subtotal/discount/total for you
- `status` uses uppercase enum values like `SUBMITTED`, `APPROVED`, `CANCELLED`
- `DELETE /orders/{id}` does not remove the order; it marks status as `CANCELLED`

Recommended create payload:

```json
{
  "orderNumber": "ORD-001",
  "status": "SUBMITTED",
  "salesmanId": "uuid",
  "retailerId": "uuid",
  "dealerId": "uuid",
  "subtotal": 1000,
  "discount": 50,
  "total": 950,
  "items": [
    {
      "skuId": "uuid",
      "skuCode": "SKU-001",
      "productName": "Product Name",
      "quantity": 5,
      "unitPrice": 200,
      "discount": 0,
      "total": 1000
    }
  ]
}
```

## Payment APIs

Payment endpoints return raw DTOs except delete.

Payment DTO:

```json
{
  "id": "uuid",
  "paymentNumber": "PAY-001",
  "retailerId": "uuid",
  "dealerId": "uuid",
  "salesmanId": "uuid",
  "amount": 5000,
  "method": "cash",
  "status": "PENDING",
  "receivedAt": "2026-04-26T10:00:00",
  "verifiedAt": null,
  "verifiedBy": null
}
```

Routes:

- `GET /payments`
- `GET /payments/{id}`
- `POST /payments`
- `POST /payments/{id}/verify`
- `DELETE /payments/{id}`

Frontend notes:

- `status` must use uppercase enum values like `PENDING`, `VERIFIED`, `REJECTED`
- `DELETE /payments/{id}` currently does not delete anything
- It only returns success if the payment exists

Recommended create payload:

```json
{
  "paymentNumber": "PAY-001",
  "retailerId": "uuid",
  "dealerId": "uuid",
  "salesmanId": "uuid",
  "amount": 5000,
  "method": "cash",
  "status": "PENDING",
  "receivedAt": "2026-04-26T10:00:00"
}
```

## Notification APIs

Notification endpoints return `ApiResponse`.

Notification DTO:

```json
{
  "id": "uuid",
  "userId": "uuid",
  "title": "Payment received",
  "message": "A new payment was created",
  "type": "payment",
  "priority": "normal",
  "read": false,
  "readAt": null,
  "createdAt": "2026-04-26T10:00:00"
}
```

Notification preference DTO:

```json
{
  "type": "payment",
  "email": true,
  "push": true,
  "sms": false
}
```

Routes:

- `GET /notifications?unreadOnly=false`
- `GET /notifications/{id}`
- `POST /notifications`
- `POST /notifications/{id}/read`
- `POST /notifications/read-all`
- `DELETE /notifications/{id}`
- `GET /notifications/preferences`
- `PUT /notifications/preferences`

Create notification request body:

```json
{
  "title": "Payment received",
  "message": "A new payment was created",
  "type": "payment",
  "priority": "normal"
}
```

`POST /notifications/read-all` response `data` is an integer count of updated notifications.

## Configuration APIs

Configuration endpoints return `ApiResponse`.

Routes:

- `GET /configuration`
- `PUT /configuration`

Response `data`:

```json
{
  "id": "uuid",
  "modules": "{\"orders\":true}",
  "regions": "[\"Dhaka\",\"Chattogram\"]",
  "branding": "{\"appName\":\"DistribNet\"}",
  "workflows": "{\"approvalRequired\":true}",
  "geoLocation": "{\"enabled\":true}",
  "customFields": "{\"dealer\":[\"tradeLicense\"]}",
  "createdAt": "2026-04-26T10:00:00",
  "updatedAt": "2026-04-26T10:30:00"
}
```

Frontend note:

- These config sections are stored as JSON strings, not nested JSON objects
- Parse them with `JSON.parse(...)` when present
- Stringify them before sending in `PUT /configuration`

Recommended update payload:

```json
{
  "modules": "{\"orders\":true,\"payments\":true}",
  "regions": "[\"Dhaka\",\"Khulna\"]",
  "branding": "{\"appName\":\"DistribNet\",\"primaryColor\":\"#1A3C5E\"}",
  "workflows": "{\"approvalRequired\":true}",
  "geoLocation": "{\"enabled\":true}",
  "customFields": "{\"dealer\":[\"tradeLicense\"]}"
}
```

## AI APIs

### `GET /ai/insights`

Returns a raw array, not `ApiResponse`.

Response:

```json
[
  {
    "period": "2026-Q2",
    "predictedRevenue": 550000.0
  },
  {
    "period": "2026-Q3",
    "predictedRevenue": 620000.0
  }
]
```

## Approval APIs

Approval endpoints return `ApiResponse`.

Access note:

- These endpoints require authenticated users with role `SUPER_ADMIN` or `TENANT_ADMIN`

Approval summary DTO:

```json
{
  "id": "uuid",
  "sourceType": "ADMIN_PORTAL",
  "entityType": "USER",
  "actionType": "CREATE",
  "status": "PENDING",
  "summary": "Create user: John Doe",
  "reviewNote": null,
  "requestedByUserId": "uuid",
  "requestedByName": "Admin User",
  "reviewedByUserId": null,
  "reviewedByName": null,
  "reviewedAt": null,
  "createdAt": "2026-04-26T10:00:00",
  "updatedAt": "2026-04-26T10:00:00"
}
```

Routes:

- `GET /approvals/admin-requests?status=PENDING`
- `GET /approvals/customer-requests?status=PENDING`
- `POST /approvals/customer-requests`
- `POST /approvals/{approvalRequestId}/approve`
- `POST /approvals/{approvalRequestId}/reject`

Customer approval request payload:

```json
{
  "requestType": "NEW_CUSTOMER",
  "customerName": "Bismillah Store",
  "customerEmail": "store@example.com",
  "customerPhone": "01700000000",
  "summary": "Need approval for onboarding",
  "details": "Retailer submitted complete onboarding information."
}
```

Approve or reject payload:

```json
{
  "reviewNote": "Approved after verification"
}
```

## Admin User Management APIs

These endpoints return `ApiResponse`.

Access note:

- Require role `SUPER_ADMIN` or `TENANT_ADMIN`
- Create/update actions do not apply immediately
- They create approval requests first

### Permissions

- `GET /admin/permissions`

Response `data` is `PermissionSummaryDto[]`:

```json
{
  "id": "uuid",
  "code": "dealer.read",
  "name": "Dealer Read",
  "module": "dealer",
  "description": "Read dealers"
}
```

### Roles

- `GET /admin/roles`
- `POST /admin/roles`
- `PUT /admin/roles/{roleId}`

Role summary DTO:

```json
{
  "id": "uuid",
  "code": "REGIONAL_MANAGER",
  "name": "Regional Manager",
  "description": "Regional operations role",
  "baseRole": "MANAGER",
  "active": true,
  "permissions": [
    {
      "id": "uuid",
      "code": "dashboard.read",
      "name": "Dashboard Read",
      "module": "dashboard",
      "description": "Read dashboard"
    }
  ],
  "createdAt": "2026-04-26T10:00:00",
  "updatedAt": "2026-04-26T10:00:00"
}
```

Create role payload:

```json
{
  "code": "REGIONAL_MANAGER",
  "name": "Regional Manager",
  "description": "Regional operations role",
  "baseRole": "MANAGER",
  "active": true,
  "permissionIds": ["uuid-1", "uuid-2"]
}
```

Update role payload:

```json
{
  "name": "Regional Manager",
  "description": "Updated description",
  "baseRole": "MANAGER",
  "active": true,
  "permissionIds": ["uuid-1", "uuid-2"]
}
```

### Users

- `GET /admin/users`
- `GET /admin/users/{userId}`
- `POST /admin/users`
- `PUT /admin/users/{userId}`
- `PATCH /admin/users/{userId}/role`

User summary DTO:

```json
{
  "id": "uuid",
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "01700000000",
  "region": "Dhaka",
  "status": "active",
  "baseRole": "MANAGER",
  "roleId": "uuid",
  "roleCode": "REGIONAL_MANAGER",
  "roleName": "Regional Manager",
  "lastLoginAt": "2026-04-26T10:00:00",
  "createdAt": "2026-04-20T10:00:00",
  "updatedAt": "2026-04-26T10:00:00"
}
```

Create user payload:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "01700000000",
  "password": "password123",
  "region": "Dhaka",
  "status": "ACTIVE",
  "roleId": "uuid"
}
```

Update user payload:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "01700000000",
  "region": "Dhaka",
  "status": "ACTIVE",
  "roleId": "uuid"
}
```

Assign role payload:

```json
{
  "roleId": "uuid"
}
```

Create/update/assign-role responses:

```json
{
  "success": true,
  "data": {
    "message": "User creation submitted for approval",
    "approvalRequest": {
      "id": "uuid",
      "status": "PENDING"
    }
  },
  "message": "User creation request submitted successfully"
}
```

## Legacy User APIs

These are separate from `/api/v1/admin/users`.

Routes:

- `GET /users`
- `GET /users/{id}`
- `POST /users`

These return raw entity data and are less frontend-safe than the admin APIs.

Recommendation:

- Prefer `/api/v1/admin/users` for frontend work
- Use `/api/v1/users` only if you specifically need the legacy endpoint behavior

## TypeScript Interface Suggestions

```ts
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  errors?: Array<{
    code: string;
    field?: string | null;
    message: string;
  }>;
  meta?: Record<string, unknown>;
}
```

```ts
export interface AuthSession {
  accessToken: string | null;
  refreshToken: string | null;
  tokenType: string;
  user: {
    id: string;
    tenantId: string;
    name: string;
    email: string;
    phone: string | null;
    role: string;
    status: string;
    region: string | null;
    lastLoginAt: string | null;
    createdAt: string | null;
    updatedAt: string | null;
  };
  tenant: {
    id: string;
    name: string;
    domain: string;
    plan: string | null;
    primaryColor: string | null;
    appName: string | null;
  };
}
```

## Practical Frontend Notes

- Use uppercase enum values for most create/update requests on CRUD modules.
- Do not send `tenantId` in business payloads.
- Be ready for `null` values in optional relations like `dealerId`, `salesmanId`, `managerId`, `verifiedBy`, and `phone`.
- There is no pagination, filter, or search support implemented yet, even if the frontend plans for it.
- Dates are a mix of `LocalDate` and `LocalDateTime`, so treat date strings carefully in the UI.
- The current H2 in-memory setup means data resets whenever the app restarts.

## Backend Gaps The Frontend Should Know

- Response format is inconsistent across modules.
- Some delete endpoints are soft actions, not true deletes.
- `DELETE /payments/{id}` does not actually delete.
- Order creation currently trusts frontend-calculated totals.
- Retailer `region` and `gps` are strings instead of structured objects.
- Configuration JSON sections are stored as strings.
- List endpoints currently return full arrays without paging.

## Recommended Frontend Priority

Best modules to integrate first:

1. Auth
2. Dashboard
3. Dealers
4. Products
5. Retailers
6. Orders
7. Payments
8. Notifications
9. Admin user management
10. Approvals

