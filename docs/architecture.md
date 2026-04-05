# Architecture

## High-Level Architecture

The project follows a clean frontend layering pattern:

1. `src/app`
   Route-level pages and layouts
2. `src/components`
   Reusable UI and module-specific presentation components
3. `src/lib/hooks`
   React Query hooks and feature-facing data access hooks
4. `src/lib/services`
   Service functions that encapsulate data retrieval and mutation
5. `src/lib/mock`
   Mock datasets used by the services
6. `src/lib/types`
   Shared domain and API types
7. `src/constants`
   Routes, roles, access matrices
8. `src/lib/stores`
   Client-side auth/session state

## Data Flow

The typical data flow is:

`Page -> Hook -> Service -> Mock Data -> Hook Cache -> UI`

Example:

`OrdersPage -> useOrders() -> orderService.getAll() -> mockOrders`

Mutations follow:

`Form/Button Action -> Mutation Hook -> Service Mutation -> Mock Array Update -> Query Invalidation -> UI Refresh`

## Authentication Model

Current auth is mock-based.

- Login uses React Hook Form and Zod validation.
- Corporate email is enforced by validation rules.
- `mockLogin()` in Zustand simulates an API call and stores a mock session.
- `DashboardLayout` guards authenticated routes and redirects unauthenticated users to `/login`.
- Session persistence uses Zustand `persist` under the key `distribnet-auth`.

## State Management

There are two main state patterns:

- server-style async state with React Query
- local UI/session state with React state and Zustand

React Query is responsible for:

- list/detail fetching
- cache keys
- invalidation after create/update/delete or approve/reject actions

Zustand is responsible for:

- auth session
- auth loading state
- persisted login status

## Service Strategy

There are two service styles in the codebase:

- dedicated services in `src/lib/services`
- service-like logic colocated inside `src/lib/hooks/useModules2.ts`

Current split:

- `dashboard.service.ts` handles dashboard analytics
- `modules.service.ts` handles dealers, retailers, salesmen, orders, payments
- `useModules2.ts` contains products, notifications, configuration, users, and AI insight service logic

This works, but it is slightly inconsistent and could later be normalized so all feature services live in `src/lib/services`.

## UI Composition Pattern

Feature pages are composed from:

- shared layout shell
- shared table and filter components
- summary KPI cards
- slide-out detail/edit panels
- confirmation dialogs
- module-specific charts/widgets

This produces a consistent admin-dashboard user experience across modules.

## Routing Model

The route system is App Router based.

- `src/app/layout.tsx` is the root layout
- `src/app/(auth)` contains authentication screens
- `src/app/(dashboard)` contains protected pages

The dashboard layout includes:

- `Sidebar`
- `Topbar`
- dark mode toggle
- route guard

## Multi-Tenant Design Signals

The code clearly models multi-tenancy conceptually through:

- `tenantId` on business entities
- `TenantInfo` in auth session
- tenant configuration model
- tenant branding fields
- role-based access definitions

However, tenant isolation is not yet enforced through real backend APIs in this repository.
