# Current State And Gaps

## What Is Implemented Well

- clear App Router structure
- strong visual consistency across modules
- solid TypeScript domain modeling
- good use of React Query cache keys and invalidation
- reusable table, panel, dialog, and summary UI patterns
- realistic mock data for demos and stakeholder reviews
- role and module access definitions already exist

## Current Architectural Constraints

- most data is mock-only and stored in memory
- page refresh resets mutated mock collections to source file defaults
- no real API client integration is present
- no real authentication provider is connected
- no server-side authorization enforcement exists
- no database persistence exists in this repo

## Module-Level Gaps

### Retailers

- create panel is UI-only
- no create/update/delete retailer mutation flow

### Salesmen

- create panel is UI-only
- no create/update/delete salesman mutation flow

### Orders

- create order flow is not implemented
- fulfillment stage transitions beyond approve/reject are display-only

### Payments

- record payment works in mock mode only
- verification workflow is modeled but not fully interactive

### Notifications

- preference changes are local UI state only
- no persistence for preferences

### Configuration

- geo zone edits are local component state and toast-based
- some geo changes are not tied back into tenant config persistence

## Codebase Consistency Gaps

- service logic is split between `src/lib/services` and `src/lib/hooks/useModules2.ts`
- some characters in source comments and UI strings show encoding issues
- `git status` from this folder resolves above the repo root, which suggests this directory may not be initialized as its own clean git repository

## Suggested Next Steps

1. Introduce a shared API client layer and replace `USE_MOCK` paths gradually.
2. Move product, notification, config, user, and AI service logic into dedicated service files.
3. Implement missing create/update/delete flows for retailers and salesmen.
4. Add real auth and backend session validation.
5. Enforce role and permission checks in UI routing and backend APIs.
6. Add tests for services, hooks, and critical workflows.
7. Fix text encoding issues in source files to avoid broken characters in UI and docs.

## Good Backend Integration Targets

Priority APIs to implement first:

- auth/login/session
- dashboard summary endpoints
- dealer CRUD
- retailer CRUD
- salesman CRUD and attendance
- order list/detail/approve/reject/create
- payment list/create/verify/ledger
- product CRUD
- tenant config get/update
- user management and audit log
- notifications and preferences
