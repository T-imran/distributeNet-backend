# Project Overview

## What This Project Is

DistribuNet is a dashboard-style distribution management platform for Bangladesh-focused sales and channel operations. The UI presents a multi-tenant SaaS product for managing the full distribution chain from dealer to retailer, with reporting and AI insight features.

The current repository is a frontend-heavy implementation with mock services. It is suitable for:

- product demo
- UX validation
- frontend architecture setup
- backend contract planning

It is not yet a production-connected system because most business data is served from local mock datasets.

## Primary Business Goals

The application is built to help an organization:

- manage dealers and their credit exposure
- manage retailers and track visit history
- monitor salesman performance and attendance
- process orders through approval and fulfillment stages
- record and review payments, ledgers, and aging buckets
- manage product catalog and stock state
- review analytics and AI-generated operational signals
- configure tenant-level platform behavior
- manage user access and notification behavior

## Product Positioning

The UI and copy suggest the product targets:

- FMCG or similar distribution businesses
- multi-region operations
- field-force-driven order and collection workflows
- multi-tenant organizations with role-based access

## Technical Snapshot

- Framework: Next.js 14 App Router
- Language: TypeScript
- UI: React + Tailwind CSS
- Data fetching: TanStack React Query
- Local auth/session state: Zustand persist store
- Forms/validation: React Hook Form + Zod
- Charts: Recharts
- Notifications: Sonner toast
- Data source today: mock data and in-memory services

## Runtime Model

The app uses a client-side dashboard model:

1. User logs in through a mock authentication flow.
2. Session is stored in a persisted Zustand store.
3. Protected dashboard layout checks auth state and redirects to `/login` when needed.
4. Feature pages call React Query hooks.
5. Hooks call service functions.
6. Services return mock data with simulated delays.
7. Pages render data tables, charts, panels, and forms around that data.

## Top-Level Route Groups

- `(auth)` for login
- `(dashboard)` for the authenticated application shell

## Module Coverage

Core operational modules:

- Dashboard
- Dealers
- Retailers
- Salesmen
- Orders
- Payments
- Products

Insight and management modules:

- Reports
- AI Insights
- Configuration
- Users
- Notifications
