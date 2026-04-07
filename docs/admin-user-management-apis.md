# Admin User Management APIs

This document is the frontend handoff for the admin user-management screens. These APIs support:

- permission list screen
- create role screen
- edit role and permission mapping screen
- user list screen
- create user screen
- edit user screen
- assign role to user flow

Base path: `/api/v1/admin`

Auth requirement:

- bearer token required
- caller must have `SUPER_ADMIN` or `TENANT_ADMIN` base role
- all data is tenant-scoped from the authenticated session

## 1. Get Permission Catalog

`GET /api/v1/admin/permissions`

Use this to populate the permission selection section while creating or editing a role.

Response:

```json
{
  "success": true,
  "message": "Permissions fetched successfully",
  "data": [
    {
      "id": "4f2c31df-f86f-4fd4-b8ff-d4d9e1f8ef20",
      "code": "role.create",
      "name": "Create Roles",
      "module": "user-management",
      "description": "Create new custom roles"
    }
  ]
}
```

## 2. Get Role List

`GET /api/v1/admin/roles`

Use this for the role list screen and role dropdowns in the user form.

Response:

```json
{
  "success": true,
  "message": "Roles fetched successfully",
  "data": [
    {
      "id": "5d1f9cb4-d1d2-40a4-986f-7dfef8a4434f",
      "code": "SALES_MANAGER",
      "name": "Sales Manager",
      "description": "Manages field sales operations",
      "baseRole": "MANAGER",
      "active": true,
      "permissions": [
        {
          "id": "4f2c31df-f86f-4fd4-b8ff-d4d9e1f8ef20",
          "code": "user.read",
          "name": "View Users",
          "module": "user-management",
          "description": "View user records and assignments"
        }
      ],
      "createdAt": "2026-04-07T18:00:00",
      "updatedAt": "2026-04-07T18:00:00"
    }
  ]
}
```

## 3. Create Role

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

Frontend notes:

- `code` should be unique per tenant
- `baseRole` must be one of `SUPER_ADMIN`, `TENANT_ADMIN`, `MANAGER`, `SALESMAN`, `DEALER`, `RETAILER`
- `permissionIds` must come from the permission catalog API

Response:

- returns the full created role object in the same shape as `GET /roles`

## 4. Update Role

`PUT /api/v1/admin/roles/{roleId}`

Request:

```json
{
  "name": "Sales Manager",
  "description": "Updated description from admin screen",
  "baseRole": "MANAGER",
  "active": true,
  "permissionIds": [
    "4f2c31df-f86f-4fd4-b8ff-d4d9e1f8ef20"
  ]
}
```

Frontend notes:

- `code` is intentionally not editable in this API
- if `baseRole` changes, existing users under that custom role also inherit the new base role automatically

## 5. Get User List

`GET /api/v1/admin/users`

Use this for the user list grid.

Response:

```json
{
  "success": true,
  "message": "Users fetched successfully",
  "data": [
    {
      "id": "44d7f118-7c75-43d0-97d0-34c779e910d1",
      "name": "Nusrat Jahan",
      "email": "nusrat@example.com",
      "phone": "+8801712345678",
      "region": "Dhaka North",
      "status": "ACTIVE",
      "baseRole": "MANAGER",
      "roleId": "5d1f9cb4-d1d2-40a4-986f-7dfef8a4434f",
      "roleCode": "SALES_MANAGER",
      "roleName": "Sales Manager",
      "lastLoginAt": "2026-04-07T08:30:00",
      "createdAt": "2026-04-07T08:00:00",
      "updatedAt": "2026-04-07T08:30:00"
    }
  ]
}
```

## 6. Get Single User

`GET /api/v1/admin/users/{userId}`

Use this when opening an edit screen or pre-filling a detail drawer.

Response:

- same object shape as a user item from the list API

## 7. Create User

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

Frontend notes:

- `status` must be one of `ACTIVE`, `INACTIVE`, `SUSPENDED`
- `roleId` must come from the roles API
- password is required for create

Response:

- returns the full created user object

## 8. Update User

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

Frontend notes:

- this API does not change password
- use this for profile edits plus role reassignment in one save action

## 9. Assign Role Only

`PATCH /api/v1/admin/users/{userId}/role`

Request:

```json
{
  "roleId": "5d1f9cb4-d1d2-40a4-986f-7dfef8a4434f"
}
```

Use this when the UI has a quick role-change action separate from the full edit form.

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

## Validation Errors

Validation failures follow the shared API envelope:

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

Business errors such as duplicate role code, duplicate email, invalid status, or inactive role assignment return:

```json
{
  "success": false,
  "message": "A user with this email already exists for this tenant",
  "errors": []
}
```
