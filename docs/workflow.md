# Workflow

## End-To-End Business Workflow

The application models a distribution business workflow that looks like this:

1. Tenant user signs in with corporate credentials.
2. Managers review dashboard KPIs and operational alerts.
3. Dealers are onboarded and assigned to region and area.
4. Retailers are linked under dealers.
5. Salesmen are assigned to operating areas and territories.
6. Salesmen visit retailers, generate orders, and collect payments.
7. Orders move through approval and fulfillment statuses.
8. Payments are recorded, verified, and tracked in ledgers.
9. Reports and AI insights help management take action.
10. Tenant admins configure modules, workflows, users, and notification preferences.

## Authentication Workflow

Current flow:

1. User opens `/login`.
2. Form validates corporate email and password rules.
3. `mockLogin()` simulates authentication.
4. Session is stored in Zustand persist storage.
5. User is redirected to `/`.
6. Protected dashboard layout allows access while authenticated.

## Dealer Workflow

Implemented flow:

1. Search or filter dealers.
2. Create a new dealer with business and geographic details.
3. Review dealer credit position and performance.
4. Edit dealer details.
5. Deactivate dealer when needed.

Business rules visible in UI:

- dealers operate at region plus area level
- dealer records carry credit and outstanding balances

## Retailer Workflow

Implemented and partially implemented flow:

1. Browse retailers by status and tier.
2. Open retailer detail panel.
3. Review visit history and business metrics.
4. Open create retailer panel with area and territory assignment UI.

Current limitation:

- create retailer panel is present, but it does not save through a service mutation

## Salesman Workflow

Implemented and partially implemented flow:

1. Browse salesmen by status.
2. Review achievement, attendance, visits, and collections.
3. Open attendance history.
4. Open add salesman panel with seniority and territory assignment UI.

Current limitation:

- add salesman panel is present, but it does not save through a service mutation

## Order Workflow

Implemented flow:

1. Orders are listed with current status.
2. Managers can view them in list or kanban mode.
3. Submitted orders can be approved.
4. Submitted orders can be cancelled.
5. Approved and downstream statuses are displayed for fulfillment tracking.

Observed status progression model:

- submitted
- approved
- processing
- dispatched
- delivered
- completed
- cancelled
- returned

In mock implementation, only `approve` and `reject/cancel` mutations are active.

## Payment Workflow

Implemented flow:

1. User records a payment against a retailer.
2. Payment method is selected.
3. Cheque details are captured when the method is cheque.
4. Payment appears in the payments list.
5. Retailer ledger can be reviewed.
6. Aging report surfaces overdue exposure.
7. Cheque tracker shows cheque-specific status.

Observed statuses:

- payment status: `pending`, `verified`, `rejected`
- cheque status: `deposited`, `cleared`, `bounced`

## Product Workflow

Implemented flow:

1. Search and filter products.
2. Create new SKU.
3. Edit existing SKU.
4. Review price structure and stock state.

Business purpose:

- maintain sellable catalog
- monitor low stock and reorder levels

## Reporting Workflow

Implemented flow:

1. Manager selects report tab.
2. Manager chooses period and region filters.
3. Charts and tables render against dashboard/mock metrics.
4. Revenue trend can be exported to CSV.

## AI Insight Workflow

Implemented flow:

1. Open AI insights module.
2. Review predicted revenue.
3. Review churn risk dealers.
4. Review demand forecast and stockout risk.
5. Review anomaly alerts.
6. Resolve anomaly alerts.

## Admin Workflow

Implemented flow:

1. Tenant admin opens configuration.
2. Adjust module toggles, workflows, branding, geo settings, and custom fields.
3. Save configuration.
4. Manage users and user status.
5. Review audit logs.
6. Review notifications and update notification preferences.
