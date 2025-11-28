# Storage Management System - Entity Documentation

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Core Entities](#core-entities)
3. [Location Entities](#location-entities)
4. [Safety & Compliance Entities](#safety--compliance-entities)
5. [Procurement Entities](#procurement-entities)
6. [Notification Entities](#notification-entities)
7. [Entity Relationships](#entity-relationships)
8. [Database Schema](#database-schema)

---

## Architecture Overview

The system follows a **two-tier inventory model**:

```
┌─────────────────────────┐
│  StorageReference       │  ← Product Master (Catalog)
│  (What it IS)           │
└──────────┬──────────────┘
           │ One-to-Many
           ▼
┌─────────────────────────┐
│  StorageItem (Batch)    │  ← Physical Inventory
│  (Where/When/How much)  │
└─────────────────────────┘
```

### Design Patterns Used:
- **Composition Pattern**: Embedded value objects (InventoryInfo, BatchInfo, Specifications)
- **Strategy Pattern**: Different specifications for different product types
- **Repository Pattern**: JPA repositories for data access
- **Builder Pattern**: Fluent builders for complex object creation

---

## Core Entities

### 1. StorageReference

**Package:** `com.anode.storage.entity.core`
**Table:** `storage_references`
**Purpose:** Immutable product master data (catalog entry)

#### Description
StorageReference represents the definition of a product - what it is, not how much you have or where it's stored. This is the single source of truth for product information.

#### Key Characteristics
- **Immutable**: Product definition shouldn't change
- **Catalog**: One entry per unique product/SKU
- **Shared**: Referenced by multiple batches/lots

#### Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `id` | Long | Primary key | 1 |
| `name` | String | Product name | "Ethanol 99.9% ACS Grade" |
| `productType` | ProductType | Type enum | CHEMICAL |
| `description` | String (TEXT) | Detailed description | "High purity ethanol..." |
| `sku` | String | Stock keeping unit (unique) | "CHEM-ETH-999" |
| `chemicalSpec` | ChemicalSpecification | Chemical properties (embedded) | CAS: "64-17-5" |
| `hardwareSpec` | HardwareSpecification | Hardware properties (embedded) | Model: "A000066" |
| `supplier` | Supplier | Primary supplier | Sigma-Aldrich |
| `hazardClass` | HazardClass | Safety classification | GHS02 (Flammable) |
| `defaultMinStock` | Double | Minimum stock threshold | 10.0 |
| `defaultMaxStock` | Double | Maximum stock threshold | 100.0 |
| `defaultUnit` | String | Default unit of measure | "L", "pieces" |
| `shelfLifeDays` | Integer | Default shelf life | 730 (2 years) |
| `active` | Boolean | Is product active/orderable? | true |

#### Relationships

```
StorageReference
├── items: List<StorageItem> (One-to-Many)
│   └── All batches/lots of this product
│
├── sdsDocuments: List<SdsDocument> (One-to-Many)
│   └── Safety data sheets
│
├── supplier: Supplier (Many-to-One)
│   └── Primary supplier
│
└── hazardClass: HazardClass (Many-to-One)
    └── Safety classification
```

#### Business Methods

```java
// Get total quantity across all batches
double getTotalQuantity()

// Count batches
int getBatchCount()
int getAvailableBatchCount()

// Stock checking
boolean isLowStock()  // Total < defaultMinStock

// Type checking
boolean isChemical()
boolean isHardware()
```

#### Example Usage

```java
// Create chemical reference
StorageReference ethanol = new StorageReference();
ethanol.setName("Ethanol 99.9% ACS Grade");
ethanol.setProductType(ProductType.CHEMICAL);
ethanol.setSku("CHEM-ETH-999");
ethanol.setDefaultMinStock(10.0);
ethanol.setDefaultMaxStock(100.0);
ethanol.setDefaultUnit("L");
ethanol.setShelfLifeDays(730);

ChemicalSpecification spec = new ChemicalSpecification();
spec.setCasNumber("64-17-5");
spec.setFormula("C2H5OH");
ethanol.setChemicalSpec(spec);

// Query total inventory
double total = ethanol.getTotalQuantity();  // Sum of all batches
boolean needsReorder = ethanol.isLowStock();
```

---

### 2. StorageItem

**Package:** `com.anode.storage.entity.core`
**Table:** `storage_items`
**Purpose:** Physical batch/lot of a product

#### Description
StorageItem represents a specific instance of a product - a batch, lot, or individual unit. It tracks WHERE the product is, HOW MUCH you have, and WHEN it expires.

#### Key Characteristics
- **Mutable**: Quantity, location, status can change
- **Batch-Specific**: Each batch has its own expiration, lot number
- **Trackable**: Full audit trail of movements

#### Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `id` | Long | Primary key | 1 |
| `reference` | StorageReference | Product master | → "Ethanol 99.9%" |
| `batchInfo` | BatchInfo | Batch details (embedded) | Batch: "2024-001" |
| `status` | ItemStatus | Current status | AVAILABLE |
| `inventoryInfo` | InventoryInfo | Quantity tracking (embedded) | 25.0 L |
| `location` | Location | Storage location | Cabinet A1 |
| `notes` | String (TEXT) | Batch-specific notes | "Opened 2024-01-15" |

#### Embedded Objects

**BatchInfo:**
- `batchNumber`: Internal batch ID
- `lotNumber`: Supplier's lot number
- `receivedDate`: When received
- `expirationDate`: When it expires
- `manufactureDate`: Production date
- `purchaseOrderRef`: PO reference

**InventoryInfo:**
- `quantity`: Current amount
- `unit`: Unit of measure
- `minStock`: Minimum threshold
- `maxStock`: Maximum threshold

#### Status Values

| Status | Description | Use Case |
|--------|-------------|----------|
| `AVAILABLE` | Ready for use | Normal inventory |
| `RESERVED` | Reserved for project | Allocated but not used |
| `IN_USE` | Currently being used | Checked out |
| `QUARANTINED` | On hold | Quality issues, testing |
| `EXPIRED` | Past expiration | No longer usable |
| `DISPOSED` | Marked for disposal | Waste management |
| `EMPTY` | Depleted | Quantity = 0 |

#### Relationships

```
StorageItem
├── reference: StorageReference (Many-to-One) [REQUIRED]
│   └── Product master data
│
├── location: Location (Many-to-One)
│   └── Where it's stored
│
├── stockLogs: List<StockLog> (One-to-Many)
│   └── Movement history
│
├── purchaseItems: List<PurchaseItem> (One-to-Many)
│   └── Purchase records
│
└── disposalRequests: List<DisposalRequest> (One-to-Many)
    └── Disposal tracking
```

#### Business Methods

```java
// Expiration checking
boolean isExpired()
boolean isExpiringSoon(int days)

// Availability
boolean isAvailable()  // Status == AVAILABLE && !expired

// Stock management
void addQuantity(double amount)
void removeQuantity(double amount)  // Auto-marks EMPTY at 0

// Status management
void markExpired()

// Convenience getters
ProductType getProductType()    // From reference
String getProductName()         // From reference
```

#### Example Usage

```java
// Create batch of ethanol
StorageItem batch = new StorageItem();
batch.setReference(ethanolReference);

// Batch tracking
BatchInfo batchInfo = new BatchInfo();
batchInfo.setBatchNumber("2024-001");
batchInfo.setLotNumber("SUPPLIER-LOT-12345");
batchInfo.setReceivedDate(LocalDate.of(2024, 1, 15));
batchInfo.setExpirationDate(LocalDate.of(2026, 1, 15));
batch.setBatchInfo(batchInfo);

// Inventory
InventoryInfo inventory = new InventoryInfo();
inventory.setQuantity(25.0);
inventory.setUnit("L");
batch.setInventoryInfo(inventory);

// Location and status
batch.setLocation(cabinetA1);
batch.setStatus(ItemStatus.AVAILABLE);

// Use some quantity
batch.removeQuantity(10.0);  // Now 15.0 L
if (batch.getInventoryInfo().getQuantity() == 0) {
    // Auto-marked as EMPTY
}

// Check expiration
if (batch.isExpiringSoon(30)) {
    // Alert: expires in 30 days
}
```

#### Lifecycle Management

```java
@PrePersist
@PreUpdate
protected void validateBatch() {
    // Auto-initialize embedded objects
    if (batchInfo == null) batchInfo = new BatchInfo();
    if (inventoryInfo == null) inventoryInfo = new InventoryInfo();

    // Auto-update status based on expiration
    if (batchInfo.isExpired() && status == ItemStatus.AVAILABLE) {
        status = ItemStatus.EXPIRED;
    }
}
```

---

### 3. User

**Package:** `com.anode.storage.entity.core`
**Table:** `users`
**Purpose:** System users and authentication

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `email` | String | Login email (unique) |
| `passwordHash` | String | Encrypted password |
| `firstName` | String | First name |
| `lastName` | String | Last name |
| `role` | String | User role (ADMIN, USER, etc.) |

#### Relationships

```
User
├── stockLogs: List<StockLog>
├── requestedDisposals: List<DisposalRequest>
├── approvedDisposals: List<DisposalRequest>
└── notifications: List<Notification>
```

---

### 4. Supplier

**Package:** `com.anode.storage.entity.core`
**Table:** `supplier`
**Purpose:** Supplier/vendor information

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `name` | String | Supplier name |
| `contact` | String | Contact person |
| `phone` | String | Phone number |

#### Relationships

```
Supplier
├── items: List<StorageItem>
│   └── Items supplied (via StorageReference)
│
└── purchaseOrders: List<PurchaseOrder>
    └── Orders placed
```

---

### 5. ProductType (Enum)

**Package:** `com.anode.storage.entity.core`
**Purpose:** Product category classification

#### Values

| Value | Prefix | Display Name | Use Case |
|-------|--------|--------------|----------|
| `CHEMICAL` | CHEM | Chemical Product | Chemicals, solvents |
| `HARDWARE` | HW | Hardware Component | Nuts, bolts, fasteners |
| `ELECTRICAL` | ELEC | Electrical Component | Resistors, capacitors |
| `MECHANICAL` | MECH | Mechanical Component | Gears, bearings |
| `ELECTRONIC` | ELCTR | Electronic Component | ICs, microcontrollers |
| `TOOL` | TOOL | Tool | Hand tools, equipment |
| `OTHER` | OTH | Other | Miscellaneous |

#### Methods

```java
String getPrefix()        // "CHEM", "HW", etc.
String getDisplayName()   // "Chemical Product", etc.
```

#### Example

```java
ProductType type = ProductType.CHEMICAL;
String qrCode = type.getPrefix() + "-" + itemId;  // "CHEM-123"
```

---

### 6. ItemStatus (Enum)

**Package:** `com.anode.storage.entity.core`
**Purpose:** Batch/item status tracking

#### Status Transitions

```
AVAILABLE → RESERVED → IN_USE → EMPTY
    ↓           ↓
QUARANTINED  EXPIRED → DISPOSED
```

---

### 7. ChemicalSpecification (Embeddable)

**Package:** `com.anode.storage.entity.core.specification`
**Purpose:** Chemical-specific properties

#### Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `casNumber` | String | CAS Registry Number | "64-17-5" |
| `formula` | String | Chemical formula | "C2H5OH" |

#### Usage

```java
ChemicalSpecification spec = new ChemicalSpecification();
spec.setCasNumber("64-17-5");
spec.setFormula("C2H5OH");
reference.setChemicalSpec(spec);
```

---

### 8. HardwareSpecification (Embeddable)

**Package:** `com.anode.storage.entity.core.specification`
**Purpose:** Hardware/electrical/mechanical properties

#### Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `manufacturer` | String | Manufacturer name | "Arduino" |
| `modelNumber` | String | Model/part number | "A000066" |
| `serialNumber` | String | Serial number | "SN-2024-001" |
| `specifications` | String (TEXT) | Technical specs | "5V, 16MHz, ATmega328P" |

---

### 9. InventoryInfo (Embeddable)

**Package:** `com.anode.storage.entity.core`
**Purpose:** Quantity and stock level tracking

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `quantity` | Double | Current amount |
| `unit` | String | Unit of measure |
| `minStock` | Double | Minimum threshold |
| `maxStock` | Double | Maximum threshold |
| `expirationDate` | LocalDate | Expiration date |

#### Business Methods

```java
boolean isLowStock()              // quantity < minStock
boolean isExpired()               // expirationDate < today
boolean isExpiringSoon(int days)  // expires within N days
void addQuantity(double amount)
void removeQuantity(double amount)
```

---

### 10. BatchInfo (Embeddable)

**Package:** `com.anode.storage.entity.core`
**Purpose:** Batch/lot tracking information

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `batchNumber` | String | Internal batch ID |
| `lotNumber` | String | Supplier lot number |
| `receivedDate` | LocalDate | Receipt date |
| `expirationDate` | LocalDate | Expiration date |
| `manufactureDate` | LocalDate | Production date |
| `purchaseOrderRef` | String | PO reference |

#### Business Methods

```java
boolean isExpired()
boolean isExpiringSoon(int days)
long getAgeInDays()  // Days since received
```

---

## Location Entities

### 1. Site

**Package:** `com.anode.storage.entity.location`
**Table:** `site`
**Purpose:** Physical site/facility

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `name` | String | Site name |
| `address` | String (TEXT) | Physical address |

#### Relationships

```
Site
└── locations: List<Location>
    └── All storage locations at this site
```

---

### 2. Location

**Package:** `com.anode.storage.entity.location`
**Table:** `location`
**Purpose:** Specific storage location

#### Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `id` | Long | Primary key | 1 |
| `site` | Site | Parent site | Main Lab |
| `room` | String | Room identifier | "Lab 101" |
| `cabinet` | String | Cabinet identifier | "A1" |
| `shelf` | String | Shelf identifier | "Top" |
| `temperatureRange` | String | Storage temp | "2-8°C" |

#### Relationships

```
Location
├── site: Site (Many-to-One)
├── items: List<StorageItem>
└── sensorReadings: List<SensorReading>
```

#### Hierarchical Structure

```
Site
└── Location (Room)
    └── Location (Cabinet)
        └── Location (Shelf)
```

---

### 3. SensorReading

**Package:** `com.anode.storage.entity.location`
**Table:** `sensor_reading`
**Purpose:** Environmental monitoring

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `location` | Location | Monitored location |
| `temperature` | Double | Temperature (°C) |
| `humidity` | Double | Humidity (%) |
| `timestamp` | LocalDateTime | Reading time |

---

## Safety & Compliance Entities

### 1. HazardClass

**Package:** `com.anode.storage.entity.safety`
**Table:** `hazard_class`
**Purpose:** GHS hazard classification

#### Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `id` | Long | Primary key | 1 |
| `name` | String | Hazard name | "Flammable Liquid" |
| `ghsCode` | String | GHS code | "GHS02" |
| `description` | String (TEXT) | Description | "Catches fire easily..." |

---

### 2. IncompatibilityRule

**Package:** `com.anode.storage.entity.safety`
**Table:** `incompatibility_rule`
**Purpose:** Storage incompatibility rules

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `hazardClassAEntity` | HazardClass | First hazard class |
| `hazardClassBEntity` | HazardClass | Second hazard class |
| `reason` | String (TEXT) | Why incompatible |

#### Example

```java
IncompatibilityRule rule = new IncompatibilityRule();
rule.setHazardClassAEntity(flammable);  // GHS02
rule.setHazardClassBEntity(oxidizer);    // GHS03
rule.setReason("May cause fire or explosion");
```

---

### 3. StockLog

**Package:** `com.anode.storage.entity.safety`
**Table:** `stock_log`
**Purpose:** Audit trail of stock movements

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `item` | StorageItem | Item moved |
| `fromLocation` | Location | Source location |
| `toLocation` | Location | Destination |
| `amount` | Double | Quantity moved |
| `user` | User | Who moved it |
| `note` | String (TEXT) | Notes |
| `timestamp` | LocalDateTime | When |

---

### 4. SdsDocument

**Package:** `com.anode.storage.entity.safety`
**Table:** `sds_document`
**Purpose:** Safety Data Sheet management

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `reference` | StorageReference | Product reference |
| `filePath` | String | PDF location |
| `version` | String | SDS version |
| `issuedDate` | LocalDate | Issue date |
| `parsed` | Boolean | Auto-parsed? |

#### Relationships

```
SdsDocument
├── reference: StorageReference (Many-to-One)
└── sections: List<SdsSection>
```

---

### 5. SdsSection

**Package:** `com.anode.storage.entity.safety`
**Table:** `sds_section`
**Purpose:** Parsed SDS sections

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `sdsDocument` | SdsDocument | Parent SDS |
| `sectionNumber` | Integer | Section # (1-16) |
| `title` | String | Section title |
| `content` | String (TEXT) | Section text |

---

### 6. DisposalRequest

**Package:** `com.anode.storage.entity.safety`
**Table:** `disposal_request`
**Purpose:** Waste disposal tracking

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `item` | StorageItem | Item to dispose |
| `amount` | Double | Quantity |
| `reason` | String (TEXT) | Why dispose |
| `requestedBy` | User | Requester |
| `status` | String | Status |
| `requestedDate` | LocalDateTime | Request time |
| `approvedBy` | User | Approver |
| `approvedDate` | LocalDateTime | Approval time |

---

## Procurement Entities

### 1. PurchaseOrder

**Package:** `com.anode.storage.entity.procurement`
**Table:** `purchase_order`
**Purpose:** Purchase order management

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `supplier` | Supplier | Vendor |
| `orderDate` | LocalDate | Order date |
| `expectedArrival` | LocalDate | Expected delivery |
| `status` | String | Order status |
| `totalCost` | BigDecimal | Total cost |

#### Relationships

```
PurchaseOrder
├── supplier: Supplier (Many-to-One)
└── items: List<PurchaseItem>
```

---

### 2. PurchaseItem

**Package:** `com.anode.storage.entity.procurement`
**Table:** `purchase_item`
**Purpose:** Line items in purchase orders

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `order` | PurchaseOrder | Parent PO |
| `item` | StorageItem | Item ordered |
| `amount` | Double | Quantity |
| `unit` | String | Unit |
| `pricePerUnit` | BigDecimal | Unit price |

---

## Notification Entities

### 1. Notification

**Package:** `com.anode.storage.entity.notification`
**Table:** `notification`
**Purpose:** User notifications

#### Fields

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key |
| `user` | User | Recipient |
| `type` | String | Notification type |
| `message` | String (TEXT) | Message |
| `read` | Boolean | Read status |
| `createdAt` | LocalDateTime | Created time |

---

## Entity Relationships

### Complete Entity Relationship Diagram

```
┌────────────────┐
│ Site           │
└────┬───────────┘
     │ 1:N
     ▼
┌────────────────┐         ┌─────────────────┐
│ Location       │◄────────│ SensorReading   │
└────┬───────────┘ 1:N    └─────────────────┘
     │ 1:N
     ▼
┌────────────────────────────────────────────┐
│ StorageItem (Batch)                        │
│ ┌──────────────────────────────────────┐   │
│ │ BatchInfo (Embedded)                 │   │
│ ├──────────────────────────────────────┤   │
│ │ InventoryInfo (Embedded)             │   │
│ └──────────────────────────────────────┘   │
└────┬───────────────────────────────────────┘
     │ N:1
     ▼
┌────────────────────────────────────────────┐
│ StorageReference (Product Master)          │
│ ┌──────────────────────────────────────┐   │
│ │ ChemicalSpecification (Embedded)     │   │
│ ├──────────────────────────────────────┤   │
│ │ HardwareSpecification (Embedded)     │   │
│ └──────────────────────────────────────┘   │
└────┬────────┬──────────┬──────────────────┘
     │        │          │
     │ N:1    │ N:1      │ 1:N
     ▼        ▼          ▼
┌─────────┐ ┌──────────┐ ┌──────────────┐
│Supplier │ │HazardClass│ │ SdsDocument  │
└─────────┘ └──────────┘ └──────┬───────┘
                                 │ 1:N
                                 ▼
                          ┌──────────────┐
                          │ SdsSection   │
                          └──────────────┘

┌──────────────┐
│ StockLog     │◄────── StorageItem
│ User         │
│ Location     │
└──────────────┘

┌──────────────┐
│PurchaseOrder │◄────── Supplier
└──────┬───────┘
       │ 1:N
       ▼
┌──────────────┐
│PurchaseItem  │◄────── StorageItem
└──────────────┘

┌──────────────┐
│DisposalReq   │◄────── StorageItem, User
└──────────────┘

┌──────────────┐
│Notification  │◄────── User
└──────────────┘
```

---

## Database Schema

### Table Summary

| Table | Purpose | Key Relationships |
|-------|---------|-------------------|
| `storage_references` | Product catalog | → storage_items (1:N) |
| `storage_items` | Physical batches | → storage_references (N:1) |
| `users` | System users | ← notifications, stock_logs |
| `supplier` | Vendors | ← purchase_orders |
| `site` | Facilities | → locations (1:N) |
| `location` | Storage locations | ← storage_items |
| `hazard_class` | GHS classifications | ← storage_references |
| `incompatibility_rule` | Storage rules | → hazard_class (2x) |
| `stock_log` | Movement audit | → storage_items, users |
| `sds_document` | Safety sheets | → storage_references |
| `sds_section` | SDS sections | → sds_document |
| `purchase_order` | Orders | → supplier |
| `purchase_item` | Order lines | → purchase_order, storage_items |
| `disposal_request` | Disposal tracking | → storage_items, users |
| `notification` | User alerts | → users |
| `sensor_reading` | Environmental data | → location |

### Indexes

**Performance Indexes:**
```sql
-- Storage items
idx_storage_items_reference_id
idx_storage_items_batch_number
idx_storage_items_status
idx_storage_items_location_id

-- Storage references
idx_storage_references_sku
idx_storage_references_product_type
idx_storage_references_active

-- Stock logs
idx_stock_log_product_id
idx_stock_log_user_id
idx_stock_log_timestamp

-- Locations
idx_location_site_id
```

---

## Best Practices

### 1. Creating Products

```java
// Always create reference first
StorageReference ref = new StorageReference();
ref.setName("Product Name");
ref.setProductType(ProductType.CHEMICAL);
referenceRepo.save(ref);

// Then create batches
StorageItem batch = new StorageItem();
batch.setReference(ref);  // Link to master
itemRepo.save(batch);
```

### 2. Querying Inventory

```java
// Total across all batches
StorageReference ref = referenceRepo.findBySku("SKU-001").get();
double total = ref.getTotalQuantity();

// Specific batch
List<StorageItem> batches = itemRepo.findByReferenceId(ref.getId());
```

### 3. Tracking Movements

```java
// Always log movements
StockLog log = new StockLog();
log.setItem(batch);
log.setFromLocation(oldLoc);
log.setToLocation(newLoc);
log.setUser(currentUser);
logRepo.save(log);
```

### 4. Status Management

```java
// Update status appropriately
if (batch.isExpired()) {
    batch.setStatus(ItemStatus.EXPIRED);
}

if (batch.getInventoryInfo().getQuantity() <= 0) {
    batch.setStatus(ItemStatus.EMPTY);
}
```

---

## Migration History

| Version | Description |
|---------|-------------|
| V1 | Initial schema (chemical-only) |
| V2 | Refactor to storage_items with product_type |
| V3 | Add entity relationships and indexes |
| V4 | Add composition support (embedded objects) |
| V5 | Split into storage_references and storage_items |

---

## Glossary

- **Reference**: Product catalog entry (master data)
- **Item**: Physical batch/lot of a product
- **Batch**: A specific production run or receipt
- **SKU**: Stock Keeping Unit (unique product identifier)
- **GHS**: Globally Harmonized System (hazard classification)
- **SDS**: Safety Data Sheet
- **CAS**: Chemical Abstracts Service number
- **FIFO**: First In, First Out (inventory method)

---

Generated: 2025-11-28
Version: 1.0
