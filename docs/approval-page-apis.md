# Approval Page APIs

This document is the frontend handoff for the approval page.

The page has 2 tabs:

- `Admin Work`
- `Customer End Request Approval`

Base path: `/api/v1/approvals`

Auth requirement:

- bearer token required
- reviewer must have `SUPER_ADMIN` or `TENANT_ADMIN`
- requester cannot approve or reject their own request

## Tab 1: Admin Work

This tab shows admin-originated pending or processed actions like:

- add role
- edit role
- add user
- edit user
- assign role

### Get Admin Approval List

`GET /api/v1/approvals/admin-requests`

Optional query params:

- `status=PENDING`
- `status=APPROVED`
- `status=REJECTED`

Example:

`GET /api/v1/approvals/admin-requests?status=PENDING`

Response:

```json
{
  "success": true,
  "message": "Admin approval requests fetched successfully",
  "data": [
    {
      "id": "b18173ce-8d89-4537-8649-8d201285bfb7",
      "sourceType": "ADMIN_PORTAL",
      "entityType": "ROLE",
      "actionType": "CREATE",
      "status": "PENDING",
      "summary": "Create role: Sales Manager",
      "reviewNote": null,
      "requestedByUserId": "11f2dcd5-0c17-4e52-b180-4b1c1739cb66",
      "requestedByName": "Admin One",
      "reviewedByUserId": null,
      "reviewedByName": null,
      "reviewedAt": null,
      "createdAt": "2026-04-07T19:00:00",
      "updatedAt": "2026-04-07T19:00:00"
    }
  ]
}
```

Suggested table columns:

- summary
- entity type
- action type
- requested by
- created at
- status
- review note
- actions

### Approve Request

`POST /api/v1/approvals/{approvalRequestId}/approve`

Request:

```json
{
  "reviewNote": "Looks good"
}
```

Behavior:

- request status changes to `APPROVED`
- for admin work items, the actual role/user change is applied now
- the approved change becomes active in admin portal and customer-facing usage after this succeeds

### Reject Request

`POST /api/v1/approvals/{approvalRequestId}/reject`

Request:

```json
{
  "reviewNote": "Please correct the role permissions"
}
```

Behavior:

- request status changes to `REJECTED`
- no active data change is applied

## Tab 2: Customer End Request Approval

This tab is for requests coming from the customer-facing side.

Example use cases:

- customer profile change request
- customer onboarding request
- customer account update request

### Get Customer Request Approval List

`GET /api/v1/approvals/customer-requests`

Optional query params:

- `status=PENDING`
- `status=APPROVED`
- `status=REJECTED`

Example:

`GET /api/v1/approvals/customer-requests?status=PENDING`

Response:

```json
{
  "success": true,
  "message": "Customer approval requests fetched successfully",
  "data": [
    {
      "id": "8d744d47-d527-4f6e-97af-0831dc311f1d",
      "sourceType": "CUSTOMER_PORTAL",
      "entityType": "CUSTOMER_REQUEST",
      "actionType": "REQUEST",
      "status": "PENDING",
      "summary": "Retailer profile update request",
      "reviewNote": null,
      "requestedByUserId": "79e93c2f-3af2-4dc4-8ed5-208fe6802441",
      "requestedByName": "Customer Support User",
      "reviewedByUserId": null,
      "reviewedByName": null,
      "reviewedAt": null,
      "createdAt": "2026-04-07T20:00:00",
      "updatedAt": "2026-04-07T20:00:00"
    }
  ]
}
```

Suggested table columns:

- summary
- requested by
- created at
- status
- review note
- actions

### Optional API For Creating Customer-Side Approval Requests

If frontend or another internal module needs to create a customer-end approval item:

`POST /api/v1/approvals/customer-requests`

Request:

```json
{
  "requestType": "PROFILE_UPDATE",
  "customerName": "Bismillah Store",
  "customerEmail": "store@example.com",
  "customerPhone": "+8801812345678",
  "summary": "Retailer profile update request",
  "details": "Customer requested a phone number and address change."
}
```

Response:

- returns a created approval queue item with status `PENDING`

## Shared Approve/Reject Actions

Both tabs use the same action endpoints:

- `POST /api/v1/approvals/{approvalRequestId}/approve`
- `POST /api/v1/approvals/{approvalRequestId}/reject`

Returned object shape:

```json
{
  "success": true,
  "message": "Approval completed successfully",
  "data": {
    "id": "b18173ce-8d89-4537-8649-8d201285bfb7",
    "sourceType": "ADMIN_PORTAL",
    "entityType": "ROLE",
    "actionType": "CREATE",
    "status": "APPROVED",
    "summary": "Create role: Sales Manager",
    "reviewNote": "Looks good",
    "requestedByUserId": "11f2dcd5-0c17-4e52-b180-4b1c1739cb66",
    "requestedByName": "Admin One",
    "reviewedByUserId": "7f8b9a9f-2369-4d0f-aad7-2f39888ecfb2",
    "reviewedByName": "Admin Two",
    "reviewedAt": "2026-04-07T20:10:00",
    "createdAt": "2026-04-07T19:00:00",
    "updatedAt": "2026-04-07T20:10:00"
  }
}
```

## Suggested UI Flow

- Tab 1 loads `GET /api/v1/approvals/admin-requests?status=PENDING`
- Tab 2 loads `GET /api/v1/approvals/customer-requests?status=PENDING`
- each row has `Approve` and `Reject` buttons
- clicking approve/reject opens a small modal for optional note
- after action success, refresh the active tab list
- add status filter chips for `Pending`, `Approved`, and `Rejected`
