# Modules

## Dashboard

Purpose:

- executive summary of revenue, dealers, orders, payments, and AI signals

Main capabilities:

- KPI cards
- revenue and collection trend chart
- order status distribution
- top salesmen ranking
- recent orders list
- regional performance
- AI insight widget

Data source:

- `dashboardService`
- dashboard React Query hooks

## Dealers

Purpose:

- manage distributor/dealer partners

Main capabilities:

- dealer list with search and filters
- create dealer
- edit dealer
- soft deactivate dealer by setting status to `inactive`
- view credit position and commercial summary
- assign region and area

Business emphasis:

- credit limit
- outstanding balance
- order count
- retailer count

## Retailers

Purpose:

- manage point-of-sale outlets under dealers

Main capabilities:

- retailer list with status and tier filters
- retailer details
- visit history view
- area and territory assignment in create panel UI

Business emphasis:

- dealer linkage
- tier classification
- outstanding balance
- order and visit history

Current note:

- create flow UI exists, but it is not connected to a persistent create mutation yet

## Salesmen

Purpose:

- manage field agents and monitor execution performance

Main capabilities:

- salesman list
- attendance log view
- performance KPIs
- area and territory assignment in create panel UI
- seniority selection in create panel UI

Business emphasis:

- monthly target vs achieved
- visits completed
- collection amount
- attendance rate
- field check-in state

Current note:

- create flow UI exists, but it is not connected to a persistent create mutation yet

## Orders

Purpose:

- manage order pipeline from submission to delivery

Main capabilities:

- list view
- kanban view
- filter by order status
- view order line items and totals
- approve submitted orders
- reject/cancel submitted orders

Business emphasis:

- approval workflow
- retailer/dealer/salesman linkage
- item-level pricing and totals

## Payments

Purpose:

- record collections and review receivables

Main capabilities:

- payment list with filters
- record new payment
- ledger view by retailer
- aging report
- cheque tracker
- payment detail view

Business emphasis:

- payment method
- verification status
- cheque lifecycle
- ledger balance
- receivable aging

## Products

Purpose:

- manage product catalog and stock overview

Main capabilities:

- product search and category filtering
- create SKU
- edit SKU
- stock health display
- pricing display

Business emphasis:

- SKU coding
- trade price vs MRP
- stock quantity
- reorder level
- monthly sales

## Reports

Purpose:

- business analytics and export

Main capabilities:

- overview tab
- salesmen performance tab
- dealer performance tab
- order analytics tab
- CSV export from trend data

Business emphasis:

- revenue trend
- collection performance
- regional breakdown
- productivity scorecards

## AI Insights

Purpose:

- surface predictive and anomaly-based intelligence

Main capabilities:

- sales prediction
- churn risk list
- demand forecast table
- anomaly detection feed
- resolve anomaly action

Business emphasis:

- dealer churn prevention
- stockout risk
- forward revenue planning
- operational anomaly monitoring

## Configuration

Purpose:

- control tenant-level platform behavior

Main capabilities:

- module enable/disable flags
- geo zone management
- workflow settings
- branding settings
- custom field management

Business emphasis:

- tenant customization
- geographic operating model
- approval policy
- platform branding

## Users

Purpose:

- manage internal and ecosystem users

Main capabilities:

- user list
- filter by role
- create user
- edit user
- suspend/reactivate user
- audit log view

Business emphasis:

- role-based access
- operational user administration
- auditability

## Notifications

Purpose:

- keep users informed about operational and AI events

Main capabilities:

- notification feed
- unread filter
- mark read
- mark all read
- delete notification
- notification preference toggles

Business emphasis:

- timely alerts
- multi-channel preference model
- operational awareness
