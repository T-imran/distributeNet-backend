# Admin User Management APIs

This document is the frontend handoff for admin user-management screens.

Important workflow change:

- create role, update role, create user, update user, and assign role do not go live immediately
- those actions now create a pending approval request
- only after another admin approves the request will the change become active in admin portal and customer-facing areas

Base path: `/api/v1/admin`

Auth requirement:

- bearer token required
- caller must have `SUPER_ADMIN` or `TENANT_ADMIN`
- all records are tenant-scoped from the logged-in session

## Active Data APIs

These APIs return the currently approved and active records.

### GET `/api/v1/admin/permissions`

Returns the permission catalog for role forms.

### GET `/api/v1/admin/roles`

Returns only approved roles that are already active in the system.

### GET `/api/v1/admin/users`

Returns only approved user state currently active in the system.

### GET `/api/v1/admin/users/{userId}`

Returns a single approved user record.

## Submit Role Create Request

`POST /api/v1/admin/roles`

Request:

```json
{
  "code": "SALES_MANAGER",
  "name": "Sales Manager",
  "description": "Approves field user actions",
  "baseRole": "MANAGER",
  "active": true,
  "permissionIds": [
    "4f2c31df-f86f-4fd4-b8ff-d4d9e1f8ef20",
    "f3c8f08f-2c53-44a1-89be-6f50735291d8"
  ]
}
```

Response:

```json
{
  "success": true,
  "message": "Role request submitted successfully",
  "data": {
    "message": "Role creation submitted for approval",
    "approvalRequest": {
      "id": "b18173ce-8d89-4537-8649-8d201285bfb7",
      "sourceType": "ADMIN_PORTAL",
      "entityType": "ROLE",
      "actionType": "CREATE",
      "status": "PENDING",
      "summary": "Create role: Sales Manager",
      "requestedByUserId": "11f2dcd5-0c17-4e52-b180-4b1c1739cb66",
      "requestedByName": "Admin One",
      "reviewedByUserId": null,
      "reviewedByName": null,
      "reviewedAt": null,
      "createdAt": "2026-04-07T19:00:00",
      "updatedAt": "2026-04-07T19:00:00"
    }
  }
}
```

## Submit Role Update Request

`PUT /api/v1/admin/roles/{roleId}`

Request body:

```json
{
  "name": "Sales Manager",
  "description": "Updated role details",
  "baseRole": "MANAGER",
  "active": true,
  "permissionIds": [
    "4f2c31df-f86f-4fd4-b8ff-d4d9e1f8ef20"
  ]
}
```

Response:

- same pending approval response shape as role create
- actual role change becomes active only after approval

## Submit User Create Request

`POST /api/v1/admin/users`

Request:

```json
{
  "name": "Nusrat Jahan",
  "email": "nusrat@example.com",
  "phone": "+8801712345678",
  "password": "TempPass123",
  "region": "Dhaka North",
  "status": "ACTIVE",
  "roleId": "5d1f9cb4-d1d2-40a4-986f-7dfef8a4434f"
}
```

Response:

- returns pending approval response
- user is not active for login or UI usage until approved

## Submit User Update Request

`PUT /api/v1/admin/users/{userId}`

Request:

```json
{
  "name": "Nusrat Jahan",
  "email": "nusrat@example.com",
  "phone": "+8801712345678",
  "region": "Dhaka South",
  "status": "ACTIVE",
  "roleId": "5d1f9cb4-d1d2-40a4-986f-7dfef8a4434f"
}
```

Response:

- returns pending approval response
- current active record remains unchanged until approved

## Submit User Role Assignment Request

`PATCH /api/v1/admin/users/{userId}/role`

Request:

```json
{
  "roleId": "5d1f9cb4-d1d2-40a4-986f-7dfef8a4434f"
}
```

Response:

- returns pending approval response
- user keeps existing active role until approval happens

## Frontend Integration Notes

- role and user list screens should continue reading `GET /api/v1/admin/roles` and `GET /api/v1/admin/users`
- create and edit screens should show a success state like `Submitted for approval`
- after submit, frontend should redirect to the approval queue or show the returned `approvalRequest` summary
- the approval queue page should consume the dedicated approval APIs described in [approval-page-apis.md](/e:/React%20Project/backend/docs/approval-page-apis.md)

## Suggested Screen Mapping

- Add Role screen:
  - call `GET /api/v1/admin/permissions`
  - submit to `POST /api/v1/admin/roles`
- Edit Role screen:
  - call `GET /api/v1/admin/roles`
  - submit to `PUT /api/v1/admin/roles/{roleId}`
- User List screen:
  - call `GET /api/v1/admin/users`
- Add User screen:
  - call `GET /api/v1/admin/roles`
  - submit to `POST /api/v1/admin/users`
- Edit User screen:
  - call `GET /api/v1/admin/users/{userId}`
  - call `GET /api/v1/admin/roles`
  - submit to `PUT /api/v1/admin/users/{userId}`
- Quick Assign Role action:
  - call `PATCH /api/v1/admin/users/{userId}/role`
- Approval queue screen:
  - use the approval APIs from the approval doc

## Validation And Business Errors

Validation errors use the standard API response envelope.

Business errors can include:

- duplicate email
- duplicate role code
- invalid status
- invalid base role
- inactive role assignment
- requester trying to approve their own request
