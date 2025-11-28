# Storage Management System - Architecture Documentation

## System Overview

The Storage Management System is a Spring Boot application designed to manage inventory across multiple storage types (chemicals, hardware, electrical components, etc.) using a two-tier inventory model that separates product definitions from physical inventory.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENT LAYER                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Web UI     │  │  Mobile App  │  │  QR Scanner  │      │
│  │  (React)     │  │              │  │              │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                  │              │
│         └─────────────────┴──────────────────┘              │
│                          │                                   │
│                    REST API / WebSocket                      │
└─────────────────────────┼───────────────────────────────────┘
                          │
┌─────────────────────────┼───────────────────────────────────┐
│                  CONTROLLER LAYER                            │
│  ┌────────────────────┐ ┌─────────────────┐                │
│  │ StorageItem        │ │ Auth            │                │
│  │ Controller         │ │ Controller      │                │
│  └─────────┬──────────┘ └────────┬────────┘                │
└────────────┼─────────────────────┼─────────────────────────┘
             │                     │
┌────────────┼─────────────────────┼─────────────────────────┐
│                   SERVICE LAYER                              │
│  ┌─────────┴──────────┐ ┌───────┴────────┐                │
│  │ StorageItem        │ │ Expiration     │                │
│  │ Service            │ │ Service        │                │
│  └─────────┬──────────┘ └───────┬────────┘                │
│                                                              │
│  ┌─────────────────┐ ┌─────────────────┐                  │
│  │ Location        │ │ StorageRule     │                  │
│  │ Service         │ │ Service         │                  │
│  └─────────┬───────┘ └────────┬────────┘                  │
└────────────┼──────────────────┼─────────────────────────────┘
             │                  │
┌────────────┼──────────────────┼─────────────────────────────┐
│               REPOSITORY LAYER (Spring Data JPA)             │
│  ┌─────────┴────────┐  ┌─────┴──────────┐                 │
│  │ StorageReference │  │ StorageItem    │                 │
│  │ Repository       │  │ Repository     │                 │
│  └─────────┬────────┘  └────────┬───────┘                 │
│                                                              │
│  ┌──────────────────┐  ┌──────────────────┐               │
│  │ Location         │  │ StockLog         │               │
│  │ Repository       │  │ Repository       │               │
│  └─────────┬────────┘  └────────┬─────────┘               │
└────────────┼─────────────────────┼─────────────────────────┘
             │                     │
┌────────────┼─────────────────────┼─────────────────────────┐
│                    ENTITY LAYER                              │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              CORE ENTITIES                          │   │
│  │  ┌──────────────────┐  ┌──────────────────┐        │   │
│  │  │StorageReference  │  │ StorageItem      │        │   │
│  │  │  (Master Data)   │◄─┤  (Batches)       │        │   │
│  │  └────────┬─────────┘  └──────┬───────────┘        │   │
│  │           │                    │                     │   │
│  │  ┌────────▼─────┐   ┌─────────▼────┐              │   │
│  │  │ChemicalSpec  │   │ BatchInfo    │              │   │
│  │  │(Embedded)    │   │ (Embedded)   │              │   │
│  │  └──────────────┘   └──────────────┘              │   │
│  │                                                     │   │
│  │  ┌──────────────┐   ┌───────────────┐             │   │
│  │  │HardwareSpec  │   │ InventoryInfo │             │   │
│  │  │(Embedded)    │   │ (Embedded)    │             │   │
│  │  └──────────────┘   └───────────────┘             │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │           LOCATION ENTITIES                         │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────────┐     │   │
│  │  │ Site     │──┤ Location │──┤SensorReading │     │   │
│  │  └──────────┘  └──────────┘  └──────────────┘     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │         SAFETY & COMPLIANCE ENTITIES                │   │
│  │  ┌────────────┐  ┌─────────────────┐               │   │
│  │  │HazardClass │  │Incompatibility  │               │   │
│  │  │            │  │Rule             │               │   │
│  │  └────────────┘  └─────────────────┘               │   │
│  │                                                      │   │
│  │  ┌─────────────┐ ┌──────────────┐                 │   │
│  │  │ SdsDocument │ │ StockLog     │                 │   │
│  │  └─────────────┘ └──────────────┘                 │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │          PROCUREMENT ENTITIES                       │   │
│  │  ┌──────────────┐  ┌──────────────┐               │   │
│  │  │PurchaseOrder │──┤PurchaseItem  │               │   │
│  │  └──────────────┘  └──────────────┘               │   │
│  └─────────────────────────────────────────────────────┘   │
└──────────────────────────┬───────────────────────────────┘
                           │
┌──────────────────────────┼───────────────────────────────┐
│                  DATABASE LAYER                            │
│             PostgreSQL 15 + Flyway Migration               │
│                                                            │
│  Tables:                                                   │
│  • storage_references    • storage_items                  │
│  • location             • stock_log                       │
│  • sds_document         • purchase_order                  │
│  • hazard_class         • incompatibility_rule            │
│  • users                • notification                    │
└────────────────────────────────────────────────────────────┘
```

## Core Architectural Patterns

### 1. Two-Tier Inventory Model

**Problem:** Traditional single-entity approach mixes immutable product data with mutable inventory data.

**Solution:** Separate concerns using two entities:

```
┌──────────────────────┐
│ StorageReference     │  ← Immutable catalog entry
│ (Product Master)     │     "What the product IS"
└──────────┬───────────┘
           │ 1:N
           ▼
┌──────────────────────┐
│ StorageItem          │  ← Mutable inventory batch
│ (Physical Batch)     │     "Where/When/How much"
└──────────────────────┘
```

**Benefits:**
- Avoid data duplication
- Centralized product updates
- Support multiple batches per product
- Enable FIFO/FEFO inventory management
- Simplified reporting (roll-up from batches)

**Example:**
```java
// One product reference
StorageReference ethanol = {
  name: "Ethanol 99.9%",
  casNumber: "64-17-5",
  supplier: "Sigma-Aldrich"
}

// Multiple batches
StorageItem batch1 = {
  reference: ethanol,
  batchNumber: "2024-001",
  quantity: 25.0,
  expirationDate: "2026-01-15"
}

StorageItem batch2 = {
  reference: ethanol,
  batchNumber: "2024-002",
  quantity: 50.0,
  expirationDate: "2026-03-10"
}
```

### 2. Composition Over Inheritance

**Problem:** Inheritance leads to rigid hierarchies and shared state.

**Solution:** Use composition with embedded value objects.

```
StorageReference
├── @Embedded ChemicalSpecification
├── @Embedded HardwareSpecification
└── @Embedded (other specifications)

StorageItem
├── @Embedded BatchInfo
└── @Embedded InventoryInfo
```

**Benefits:**
- Flexible - add new types without changing hierarchy
- Testable - value objects are independent
- Encapsulated - business logic in the right place
- Clean - clear separation of concerns

**Example:**
```java
@Entity
class StorageItem {
    @Embedded
    private BatchInfo batchInfo;     // Composition

    @Embedded
    private InventoryInfo inventory;  // Composition

    // Clean delegation
    public boolean isExpired() {
        return batchInfo.isExpired();
    }
}
```

### 3. Repository Pattern

**Implementation:** Spring Data JPA repositories

**Structure:**
```java
public interface StorageReferenceRepository
    extends JpaRepository<StorageReference, Long> {

    // Query methods
    Optional<StorageReference> findBySku(String sku);
    List<StorageReference> findByProductType(ProductType type);

    // Custom queries
    @Query("SELECT r FROM StorageReference r WHERE r.active = true")
    List<StorageReference> findAllActive();
}
```

**Benefits:**
- Abstraction over database
- Type-safe queries
- Derived query methods
- Custom JPQL/native queries

### 4. Domain-Driven Design

**Aggregates:**
```
StorageReference (Aggregate Root)
└── StorageItem (Entities)
    └── StockLog (Entities)
```

**Value Objects:**
- `BatchInfo`
- `InventoryInfo`
- `ChemicalSpecification`
- `HardwareSpecification`

**Entities:**
- Have identity (ID)
- Mutable lifecycle
- Persistence

**Value Objects:**
- No identity
- Immutable (should be)
- Embedded in entities

## Data Flow

### Creating a New Product & Batch

```
Client Request (POST /api/references)
         │
         ▼
┌─────────────────────┐
│ Controller          │
│ • Validate input    │
│ • Map DTO → Entity  │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Service Layer       │
│ • Business logic    │
│ • Transactions      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Repository          │
│ • Save entity       │
│ • Flush to DB       │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Database            │
│ • INSERT            │
│ • Commit            │
└─────────────────────┘
```

### Querying Total Inventory

```
Client Request (GET /api/references/1/total)
         │
         ▼
Controller → Service
         │
         ▼
    Repository.findById(1)
         │
         ▼
    Load StorageReference
         │
         ▼
    Lazy load items (batches)
         │
         ▼
    Calculate sum: items.stream()
                    .mapToDouble(...)
                    .sum()
         │
         ▼
    Return total
```

## Database Design

### Normalization Strategy

**3NF (Third Normal Form):**
- Product master data in `storage_references`
- Batch data in `storage_items`
- No transitive dependencies

**Denormalization:**
- Embedded objects (BatchInfo, InventoryInfo) for performance
- Avoids joins for commonly accessed data

### Indexing Strategy

**Primary Indexes:**
```sql
-- Primary keys (auto-indexed)
storage_references.id
storage_items.id
location.id
...

-- Unique constraints
storage_references.sku
users.email
```

**Foreign Key Indexes:**
```sql
storage_items.reference_id
storage_items.location_id
stock_log.item_id
purchase_item.reference_id
```

**Query Optimization Indexes:**
```sql
-- Frequently filtered columns
storage_items.status
storage_items.batch_number
storage_references.product_type
storage_references.active

-- Date range queries
storage_items.expiration_date
storage_items.received_date
```

### Migration Strategy

**Flyway Versioning:**
```
V1__init.sql                      ← Initial schema
V2__refactor_to_storage_items.sql ← Add product_type
V3__add_entity_relationships.sql  ← Add FK constraints
V4__add_composition_support.sql   ← Embedded objects
V5__split_reference_and_items.sql ← Two-tier model
```

**Benefits:**
- Version controlled schema
- Repeatable migrations
- Rollback capability
- Team synchronization

## Security Architecture

### Authentication

```
┌─────────────┐
│  Client     │
└──────┬──────┘
       │ POST /api/auth/login
       │ {email, password}
       ▼
┌─────────────────┐
│ AuthController  │
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│ UserRepository  │ ← Check credentials
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│  JwtService     │ ← Generate token
└──────┬──────────┘
       │
       ▼
  Return JWT token
```

### Authorization

```
Subsequent Requests:
Authorization: Bearer <JWT>
       │
       ▼
┌─────────────────┐
│ JWT Filter      │ ← Validate token
└──────┬──────────┘
       │
       ▼
┌─────────────────┐
│ Extract User    │ ← Set SecurityContext
└──────┬──────────┘
       │
       ▼
  Process Request
```

## Scalability Considerations

### Horizontal Scaling

```
┌──────────┐  ┌──────────┐  ┌──────────┐
│ App      │  │ App      │  │ App      │
│ Instance │  │ Instance │  │ Instance │
└────┬─────┘  └────┬─────┘  └────┬─────┘
     │            │            │
     └────────────┼────────────┘
                  │
          Load Balancer
                  │
                  ▼
         ┌─────────────────┐
         │   PostgreSQL    │
         │   (Primary)     │
         └────────┬────────┘
                  │
         Read Replicas (Optional)
```

**Stateless Design:**
- No session state in app servers
- JWT for authentication (stateless)
- All state in database

### Caching Strategy

**Potential Caching:**
```java
@Cacheable("storageReferences")
public StorageReference findBySku(String sku) {
    // Cache immutable product data
}

@CacheEvict(value = "storageReferences", key = "#sku")
public void updateReference(String sku, ...) {
    // Invalidate on update
}
```

**Don't Cache:**
- Inventory quantities (frequently changing)
- Stock logs (audit data)
- Real-time status

### Database Optimization

**Connection Pooling:**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```

**Lazy Loading:**
```java
@ManyToOne(fetch = FetchType.LAZY)
private StorageReference reference;
```

**Batch Operations:**
```java
itemRepo.saveAll(batches);  // Batch insert
```

## Monitoring & Observability

### Logging Strategy

**Layers:**
```
Controller → INFO: Request received
Service    → DEBUG: Business logic
Repository → TRACE: SQL queries
```

**Structured Logging:**
```java
log.info("Item created: id={}, ref={}, batch={}",
    item.getId(),
    item.getReference().getId(),
    item.getBatchInfo().getBatchNumber()
);
```

### Metrics to Track

**Business Metrics:**
- Total inventory value
- Low stock items count
- Expired items count
- Daily stock movements

**Technical Metrics:**
- Request latency (p50, p95, p99)
- Database query time
- API error rate
- JVM memory usage

### Health Checks

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Check DB connection
        // Check critical tables exist
        return Health.up().build();
    }
}
```

## Testing Strategy

### Unit Tests

```java
@Test
void testBatchExpiration() {
    BatchInfo batch = new BatchInfo();
    batch.setExpirationDate(LocalDate.now().minusDays(1));

    assertTrue(batch.isExpired());
}
```

### Integration Tests

```java
@SpringBootTest
@Transactional
class StorageItemServiceTest {
    @Test
    void testCreateItem() {
        StorageItem item = service.create(itemDto);
        assertNotNull(item.getId());
    }
}
```

### Repository Tests

```java
@DataJpaTest
class StorageItemRepositoryTest {
    @Test
    void testFindByBatchNumber() {
        List<StorageItem> items =
            repo.findByBatchInfoBatchNumber("2024-001");
        assertEquals(1, items.size());
    }
}
```

## Deployment Architecture

### Docker Compose (Development)

```
┌──────────────┐
│  Frontend    │:5173
└──────┬───────┘
       │
┌──────┴───────┐
│  Backend     │:8080
└──────┬───────┘
       │
┌──────┴───────┐
│  PostgreSQL  │:5432
└──────────────┘
```

### Production (Example)

```
        Internet
            │
    ┌───────▼────────┐
    │  Load Balancer │
    └───────┬────────┘
            │
    ┌───────┴────────┐
    │  Web Server    │ ← Serve React
    │  (Nginx)       │
    └───────┬────────┘
            │
    ┌───────┴────────┐
    │  App Servers   │ ← Spring Boot
    │  (3 instances) │
    └───────┬────────┘
            │
    ┌───────┴────────┐
    │  PostgreSQL    │ ← Database
    │  (Primary +    │
    │   Replicas)    │
    └────────────────┘
```

## Technology Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 21
- **ORM:** Spring Data JPA / Hibernate
- **Database:** PostgreSQL 15
- **Migration:** Flyway
- **Security:** JWT (JJWT)
- **Build:** Maven

### Frontend
- **Framework:** React + TypeScript
- **Build:** Vite
- **State:** (TBD)

### Infrastructure
- **Containerization:** Docker
- **Orchestration:** Docker Compose (dev)
- **Database:** PostgreSQL container

## Design Decisions

### Why PostgreSQL?
- ✅ Excellent JSON support (for future flexibility)
- ✅ ACID compliance (critical for inventory)
- ✅ Mature ecosystem
- ✅ Good performance for read-heavy workloads

### Why JPA over MyBatis?
- ✅ Type-safe queries
- ✅ Object-oriented mapping
- ✅ Spring Data integration
- ✅ Automatic DDL generation

### Why Two-Tier Inventory?
- ✅ Real-world batch tracking
- ✅ FIFO/FEFO support
- ✅ Audit trail per batch
- ✅ Flexible expiration management

### Why Composition over Inheritance?
- ✅ More flexible
- ✅ Easier to test
- ✅ Avoids deep hierarchies
- ✅ Clear boundaries

## Future Enhancements

### Phase 2
- [ ] Barcode/QR code scanning
- [ ] Mobile app for warehouse
- [ ] Advanced reporting dashboard
- [ ] Email notifications

### Phase 3
- [ ] Integration with ERP systems
- [ ] Automated reordering
- [ ] ML for demand forecasting
- [ ] Multi-tenant support

### Phase 4
- [ ] IoT sensor integration
- [ ] Blockchain for audit trail
- [ ] AR for warehouse navigation
- [ ] Voice-activated inventory

---

**Document Version:** 1.0
**Last Updated:** 2025-11-28
**Status:** Current
