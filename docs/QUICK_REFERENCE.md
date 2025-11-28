# Storage Management System - Quick Reference Guide

## Quick Start

### Creating a Chemical Product

```java
// 1. Create the product reference (catalog entry)
StorageReference ethanol = new StorageReference();
ethanol.setName("Ethanol 99.9% ACS Grade");
ethanol.setProductType(ProductType.CHEMICAL);
ethanol.setSku("CHEM-ETH-999");
ethanol.setDefaultMinStock(10.0);
ethanol.setDefaultMaxStock(100.0);
ethanol.setDefaultUnit("L");

ChemicalSpecification spec = new ChemicalSpecification();
spec.setCasNumber("64-17-5");
spec.setFormula("C2H5OH");
ethanol.setChemicalSpec(spec);

ethanol.setSupplier(sigmaAldrich);
ethanol.setHazardClass(flammableClass);

referenceRepo.save(ethanol);

// 2. Create a batch (physical inventory)
StorageItem batch = new StorageItem();
batch.setReference(ethanol);

BatchInfo batchInfo = new BatchInfo();
batchInfo.setBatchNumber("2024-001");
batchInfo.setReceivedDate(LocalDate.now());
batchInfo.setExpirationDate(LocalDate.now().plusYears(2));
batch.setBatchInfo(batchInfo);

InventoryInfo inventory = new InventoryInfo();
inventory.setQuantity(25.0);
inventory.setUnit("L");
batch.setInventoryInfo(inventory);

batch.setLocation(cabinetA1);
batch.setStatus(ItemStatus.AVAILABLE);

itemRepo.save(batch);
```

### Creating Hardware

```java
// 1. Create reference
StorageReference arduino = new StorageReference();
arduino.setName("Arduino Uno R3");
arduino.setProductType(ProductType.HARDWARE);
arduino.setSku("HW-ARD-UNO-R3");

HardwareSpecification spec = new HardwareSpecification();
spec.setManufacturer("Arduino");
spec.setModelNumber("A000066");
arduino.setHardwareSpec(spec);

referenceRepo.save(arduino);

// 2. Create batch
StorageItem batch = new StorageItem();
batch.setReference(arduino);

InventoryInfo inventory = new InventoryInfo();
inventory.setQuantity(50.0);
inventory.setUnit("pieces");
batch.setInventoryInfo(inventory);

batch.setStatus(ItemStatus.AVAILABLE);
itemRepo.save(batch);
```

## Common Queries

### Find Total Inventory

```java
// By SKU
StorageReference ref = referenceRepo.findBySku("CHEM-ETH-999").get();
double total = ref.getTotalQuantity();
int batches = ref.getBatchCount();

// By product type
List<StorageReference> chemicals = referenceRepo.findByProductType(ProductType.CHEMICAL);
```

### Find Specific Batches

```java
// All batches of a product
List<StorageItem> batches = itemRepo.findByReferenceId(referenceId);

// Available batches only
List<StorageItem> available = itemRepo.findByReferenceIdAndStatus(
    referenceId, ItemStatus.AVAILABLE);

// By batch number
StorageItem batch = itemRepo.findByBatchInfoBatchNumber("2024-001").get(0);

// By location
List<StorageItem> inCabinet = itemRepo.findByLocationId(locationId);
```

### Find Expiring Items

```java
// Items expiring in next 30 days
LocalDate now = LocalDate.now();
LocalDate soon = now.plusDays(30);
List<StorageItem> expiring = itemRepo.findByExpirationDateBetween(now, soon);

// Expired but not marked
List<StorageItem> expired = itemRepo.findExpiredNotMarked();
```

### Low Stock Check

```java
// Check specific product
StorageReference ref = referenceRepo.findById(refId).get();
if (ref.isLowStock()) {
    // Total quantity < defaultMinStock
    // Send reorder alert
}

// Find all low stock products
List<StorageReference> lowStock = referenceRepo.findAll().stream()
    .filter(StorageReference::isLowStock)
    .collect(Collectors.toList());
```

## Common Operations

### Stock Movement

```java
// Move batch to new location
StorageItem batch = itemRepo.findById(batchId).get();
Location oldLocation = batch.getLocation();
Location newLocation = locationRepo.findById(newLocationId).get();

batch.setLocation(newLocation);
itemRepo.save(batch);

// Create audit log
StockLog log = new StockLog();
log.setItem(batch);
log.setFromLocation(oldLocation);
log.setToLocation(newLocation);
log.setAmount(batch.getInventoryInfo().getQuantity());
log.setUser(currentUser);
log.setNote("Moved to storage");
stockLogRepo.save(log);
```

### Using Inventory

```java
// Remove quantity from batch
StorageItem batch = itemRepo.findById(batchId).get();
batch.removeQuantity(5.0);  // Auto-marks EMPTY if quantity hits 0
itemRepo.save(batch);

// Add quantity
batch.addQuantity(10.0);
itemRepo.save(batch);
```

### Batch Status Updates

```java
// Mark as expired
batch.markExpired();  // Sets status to EXPIRED
itemRepo.save(batch);

// Reserve for project
batch.setStatus(ItemStatus.RESERVED);
batch.setNotes("Reserved for Project X");
itemRepo.save(batch);

// Mark as disposed
batch.setStatus(ItemStatus.DISPOSED);
itemRepo.save(batch);
```

### Disposal Request

```java
DisposalRequest request = new DisposalRequest();
request.setItem(batch);
request.setAmount(batch.getInventoryInfo().getQuantity());
request.setReason("Expired");
request.setRequestedBy(currentUser);
request.setStatus("PENDING");
disposalRepo.save(request);
```

## Repository Methods

### StorageReferenceRepository

```java
findBySku(String sku)
findByProductType(ProductType type)
findByActive(Boolean active)
findAllActive()
findActiveByType(ProductType type)
findByNameContainingIgnoreCase(String name)
```

### StorageItemRepository

```java
findByReferenceId(Long referenceId)
findByStatus(ItemStatus status)
findAllAvailable()
findByBatchInfoBatchNumber(String batchNumber)
findByLocationId(Long locationId)
findByExpirationDateBetween(LocalDate start, LocalDate end)
findExpiredNotMarked()
findByReferenceIdAndStatus(Long refId, ItemStatus status)
```

### LocationRepository

```java
findBySiteId(Long siteId)
findByRoom(String room)
```

### SupplierRepository

```java
findByName(String name)
```

## Entity Status Flow

### StorageItem Status Transitions

```
NEW → AVAILABLE → IN_USE → EMPTY
        ↓            ↓
     RESERVED    DISPOSED
        ↓
  QUARANTINED → AVAILABLE
        ↓
     EXPIRED → DISPOSED
```

## Field Mapping

### StorageReference → Database

| Java Field | Database Column | Type |
|------------|-----------------|------|
| `name` | `name` | VARCHAR(255) |
| `productType` | `product_type` | VARCHAR(20) |
| `sku` | `sku` | VARCHAR(100) |
| `chemicalSpec.casNumber` | `cas_number` | VARCHAR(50) |
| `chemicalSpec.formula` | `formula` | VARCHAR(255) |
| `hardwareSpec.manufacturer` | `manufacturer` | VARCHAR(255) |
| `defaultMinStock` | `default_min_stock` | DECIMAL |

### StorageItem → Database

| Java Field | Database Column | Type |
|------------|-----------------|------|
| `reference.id` | `reference_id` | INTEGER |
| `batchInfo.batchNumber` | `batch_number` | VARCHAR(100) |
| `batchInfo.lotNumber` | `lot_number` | VARCHAR(100) |
| `batchInfo.receivedDate` | `received_date` | DATE |
| `batchInfo.expirationDate` | `expiration_date` | DATE |
| `status` | `status` | VARCHAR(20) |
| `inventoryInfo.quantity` | `quantity` | DECIMAL |
| `inventoryInfo.unit` | `unit` | VARCHAR(20) |

## Common Patterns

### FIFO (First In, First Out)

```java
// Get oldest batch first
List<StorageItem> batches = itemRepo.findByReferenceIdAndStatus(
    refId, ItemStatus.AVAILABLE);

StorageItem oldestBatch = batches.stream()
    .min(Comparator.comparing(
        item -> item.getBatchInfo().getReceivedDate()))
    .orElse(null);
```

### FEFO (First Expired, First Out)

```java
// Get batch expiring soonest
StorageItem soonestExpiring = batches.stream()
    .filter(item -> item.getBatchInfo().getExpirationDate() != null)
    .min(Comparator.comparing(
        item -> item.getBatchInfo().getExpirationDate()))
    .orElse(null);
```

### Batch Consolidation

```java
// Find all batches of same product at same location
List<StorageItem> sameBatches = itemRepo.findByReferenceId(refId).stream()
    .filter(item -> item.getLocation().getId().equals(locationId))
    .filter(item -> item.getStatus() == ItemStatus.AVAILABLE)
    .collect(Collectors.toList());

// Combine into one batch (merge logic)
```

## Safety Checks

### Check Hazard Compatibility

```java
StorageReference item1 = refRepo.findById(ref1Id).get();
StorageReference item2 = refRepo.findById(ref2Id).get();

if (item1.getHazardClass() != null && item2.getHazardClass() != null) {
    Optional<String> incompatible = ruleService.checkCompatibility(
        item1.getHazardClass().getId().intValue(),
        item2.getHazardClass().getId().intValue()
    );

    if (incompatible.isPresent()) {
        // Cannot store together: incompatible.get()
    }
}
```

### Check Expiration

```java
// Auto-check and mark expired batches
List<StorageItem> expired = itemRepo.findExpiredNotMarked();
for (StorageItem item : expired) {
    item.markExpired();
}
itemRepo.saveAll(expired);
```

## Reporting Queries

### Total Inventory Value

```java
@Query("SELECT SUM(i.inventoryInfo.quantity * pi.pricePerUnit) " +
       "FROM StorageItem i " +
       "JOIN i.purchaseItems pi " +
       "WHERE i.status = 'AVAILABLE'")
BigDecimal getTotalInventoryValue();
```

### Items by Location

```java
@Query("SELECT l.name, COUNT(i), SUM(i.inventoryInfo.quantity) " +
       "FROM StorageItem i " +
       "JOIN i.location l " +
       "GROUP BY l.id")
List<Object[]> getInventoryByLocation();
```

### Expiring Soon Report

```java
@Query("SELECT r.name, i.batchInfo.batchNumber, i.batchInfo.expirationDate " +
       "FROM StorageItem i " +
       "JOIN i.reference r " +
       "WHERE i.batchInfo.expirationDate BETWEEN :start AND :end " +
       "ORDER BY i.batchInfo.expirationDate")
List<Object[]> getExpiringReport(LocalDate start, LocalDate end);
```

## Error Handling

### Common Exceptions

```java
try {
    StorageItem item = itemRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Item not found: " + id));
} catch (RuntimeException e) {
    // Handle not found
}

// Better approach - use Optional
Optional<StorageItem> item = itemRepo.findById(id);
if (item.isPresent()) {
    // Use item.get()
} else {
    // Handle not found
}
```

### Validation

```java
@PrePersist
@PreUpdate
void validate() {
    if (reference == null) {
        throw new IllegalStateException("StorageItem must have a reference");
    }
    if (inventoryInfo == null || inventoryInfo.getQuantity() == null) {
        throw new IllegalStateException("Quantity is required");
    }
}
```

## Tips & Tricks

1. **Always use Reference for master data**: Don't duplicate product info
2. **One batch per receipt**: Create new batch for each delivery
3. **Track everything**: Use StockLog for all movements
4. **Auto-expire**: Run scheduled job to mark expired batches
5. **FIFO/FEFO**: Sort by received/expiration date for picking
6. **Lazy loading**: Use @Transactional when accessing relationships
7. **Indexing**: Query by batch_number, status, location_id frequently
8. **Status transitions**: Follow the status flow diagram
9. **Embedded objects**: Initialize in constructor or @PrePersist
10. **Bulk operations**: Use saveAll() for multiple items

## Performance Tips

```java
// Good - fetch with join
@Query("SELECT i FROM StorageItem i " +
       "JOIN FETCH i.reference " +
       "JOIN FETCH i.location " +
       "WHERE i.status = 'AVAILABLE'")
List<StorageItem> findAvailableWithDetails();

// Bad - N+1 queries
List<StorageItem> items = itemRepo.findAll();
for (StorageItem item : items) {
    String name = item.getReference().getName();  // Lazy load for each!
}
```

## JSON API Examples

### GET /api/products?productType=CHEMICAL

Returns all chemical products (references).

### GET /api/items?referenceId=1&status=AVAILABLE

Returns all available batches of product #1.

### POST /api/items

Create new batch:
```json
{
  "referenceId": 1,
  "batchInfo": {
    "batchNumber": "2024-001",
    "receivedDate": "2024-01-15",
    "expirationDate": "2026-01-15"
  },
  "inventoryInfo": {
    "quantity": 25.0,
    "unit": "L"
  },
  "locationId": 5,
  "status": "AVAILABLE"
}
```

---

**Last Updated:** 2025-11-28
**Version:** 1.0
