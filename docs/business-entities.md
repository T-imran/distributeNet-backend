# Business Entities

## Core Tenant And User Entities

### Tenant

Represents the customer organization using the platform.

Important fields:

- `id`
- `name`
- `domain`
- `plan`
- `primaryColor`
- `appName`

### User

Represents an authenticated platform user.

Important fields:

- `id`
- `tenantId`
- `name`
- `email`
- `phone`
- `role`
- `status`
- `region`

### Roles

Defined roles:

- `SUPER_ADMIN`
- `TENANT_ADMIN`
- `MANAGER`
- `SALESMAN`
- `DEALER`
- `RETAILER`

The code also defines:

- module-level access matrix
- order action permissions

## Distribution Chain Entities

### Dealer

A distribution partner operating at region and area level.

Important fields:

- `code`
- `name`
- `ownerName`
- `region`
- `creditLimit`
- `outstandingBalance`
- `totalSales`
- `orderCount`
- `retailerCount`
- `status`

### Retailer

A point-of-sale outlet served under a dealer.

Important fields:

- `dealerId`
- `dealerName`
- `tier`
- `businessType`
- `outstandingBalance`
- `totalPurchases`
- `visitCount`
- `orderCount`
- `region`
- `gps`

### Salesman

A field sales or collection agent.

Important fields:

- `userId`
- `region`
- `division`
- `district`
- `managerId`
- `monthlyTarget`
- `monthlyAchieved`
- `achievementPct`
- `visitTarget`
- `visitsCompleted`
- `ordersThisMonth`
- `collectionThisMonth`
- `attendanceRate`
- `currentStatus`

### AttendanceRecord

Daily execution record for a salesman.

Important fields:

- `date`
- `checkInAt`
- `checkOutAt`
- `checkInGps`
- `checkOutGps`
- `visitsPlanned`
- `visitsCompleted`
- `ordersPlaced`
- `collectionAmount`
- `status`

### RetailerVisit

Represents a field visit to a retailer.

Important fields:

- `retailerId`
- `salesmanId`
- `visitedAt`
- `durationMinutes`
- `outcome`
- `orderAmount`
- `gps`

## Commerce Entities

### Product

Represents a sellable SKU.

Important fields:

- `skuCode`
- `name`
- `category`
- `unit`
- `basePrice`
- `tradePrice`
- `mrp`
- `discountPct`
- `stockQty`
- `reorderLevel`
- `salesThisMonth`
- `salesTotal`

### Order

Represents a sales order placed against a retailer and dealer context.

Important fields:

- `orderNumber`
- `status`
- `salesmanId`
- `retailerId`
- `dealerId`
- `items`
- `subtotal`
- `discount`
- `total`
- `approvedBy`
- `approvedAt`

### OrderItem

Represents one line in an order.

Important fields:

- `skuId`
- `skuCode`
- `productName`
- `quantity`
- `unitPrice`
- `discount`
- `total`

### Payment

Represents a collection entry against a retailer.

Important fields:

- `paymentNumber`
- `retailerId`
- `dealerId`
- `salesmanId`
- `amount`
- `method`
- `status`
- `receivedAt`
- `verifiedAt`
- `verifiedBy`

### ChequeDetails

Attached to a payment when the payment method is cheque.

Important fields:

- `chequeNumber`
- `bankName`
- `branchName`
- `chequeDate`
- `status`
- `bounceReason`

### LedgerEntry

Represents debit or credit movement for a dealer or retailer account.

Important fields:

- `entityId`
- `entityType`
- `date`
- `description`
- `type`
- `amount`
- `balance`
- `referenceType`
- `referenceNumber`

### AgingBucket

Represents receivables grouped by age.

Important fields:

- `label`
- `minDays`
- `maxDays`
- `amount`
- `dealerCount`

## Platform Management Entities

### TenantConfig

Represents tenant-level behavior and configuration.

Contains:

- `modules`
- `regions`
- `branding`
- `workflows`
- `geoLocation`
- `customFields`

### ManagedUser

Represents admin-managed users in the tenant user management module.

### AuditLog

Represents immutable admin and system actions for traceability.

## Intelligence Entities

### SalesPrediction

Forecast data with actual and predicted revenue bands.

### ChurnRiskDealer

Dealer risk scoring model with decline indicators and recommendation.

### DemandForecast

SKU-level future demand and stockout risk projection.

### AnomalyAlert

System-generated operational anomaly with severity and resolution status.
