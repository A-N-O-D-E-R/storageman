## StorageMan what it is ? 

The storageman come from the need to unify all the storage capability in one simple software. 
It aims to track the consumption of all hardware item. 
It is not a generalize tool that can be reused but a specific storage system for non-perishable item

## Folder Structure

```
storageman/
├── back/           # Spring Boot backend (Java 21)
│   ├── src/main/java/              # Main source code
│   ├── src/test/java/              # Test sources
│   └── pom.xml                     # Maven configuration
├── front/          # React + TypeScript frontend
│   ├── e2e/                        # E2E tests (Playwright)
│   ├── src/                        # Frontend source code
│   ├── playwright.config.ts        # E2E test configuration
│   └── package.json                # NPM dependencies
└── README.md       # This file
```

## Quick Start

### Backend Setup

```bash
cd back
mvn clean install
mvn spring-boot:run
```

API will be available at `http://localhost:8080`
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/v3/api-docs

### Frontend Setup

```bash
cd front
pnpm install
pnpm dev
```

App will be available at `http://localhost:5173`

## Testing

### Frontend E2E Tests (Playwright)

```bash
cd front

# First-time setup
npx playwright install

# Run tests
pnpm test:e2e              # Headless mode
pnpm test:e2e:ui           # UI mode (recommended)
pnpm test:e2e:headed       # See browser
pnpm test:e2e:debug        # Debug mode
pnpm test:e2e:report       # View report
```

**Features:**
- ✅ Multi-browser testing (Chrome, Firefox, Safari, Mobile)
- ✅ Auto-start dev server
- ✅ Screenshots on failure
- ✅ Trace viewer for debugging

### Backend Tests (JUnit 5)

```bash
cd back

# Run all tests
mvn test

# Run specific test
mvn test -Dtest=AuthControllerTest

# Generate coverage report
mvn clean verify
```

**Test Types:**
- **Unit Tests**: Controller, Service, Repository layers
- **Integration Tests**: Full REST API testing with REST Assured
- **Test Database**: H2 in-memory for fast, isolated tests

## Dependency Management

The Dependencies are handle by ```Maven``` you could learn more in the following [documentation](https://maven.apache.org/guides/index.html)

## Hardware Structure
The project take inspiration from this [original project](https://www.thingiverse.com/thing:4544557/files), it aim to unlock a drawer from a command launch by the storageman.
To do that you will need the following part : 
- 1 [servo motor](https://www.amazon.fr/AZDelivery-SG90-Micro-Moteur-Robots-hubchrauber/dp/B07CZ42862?__mk_fr_FR=%C3%85M%C3%85%C5%BD%C3%95%C3%91&crid=3MHXM2V0V4D1L&keywords=servomoteur&qid=1661871199&sprefix=servo%2Bmoteur%2Caps%2C82&sr=8-3-spons&smid=A1X7QLRQH87QA3&spLa=ZW5jcnlwdGVkUXVhbGlmaWVyPUEzVkNZVlFVVFg5TkswJmVuY3J5cHRlZElkPUEwNzE5MTIzMko0UzVQR0xRV1M4USZlbmNyeXB0ZWRBZElkPUEwODYzMzAxMUNMSDMzN05DWEpZNiZ3aWRnZXROYW1lPXNwX2F0ZiZhY3Rpb249Y2xpY2tSZWRpcmVjdCZkb05vdExvZ0NsaWNrPXRydWU%3D&linkCode=sl1&tag=abregepartena-21&linkId=ec2a815a09a5ee1243608301836cc048&language=fr_FR&ref_=as_li_ss_tl&th=1) by drawer (3€/unit)
- Some [ESP card](https://www.amazon.fr/dp/B074Q2WM1Y?_encoding=UTF8&aaxitk=f328f70c076bd07f1b7e10c6eaf922fc&content-id=amzn1.sym.962b535b-a8b7-499f-8ba5-164fe04b0459%3Aamzn1.sym.962b535b-a8b7-499f-8ba5-164fe04b0459&hsa_cr_id=6390519720202&pd_rd_plhdr=t&pd_rd_r=060ccf5f-71ab-494b-b446-690291a850ae&pd_rd_w=7PaV3&pd_rd_wg=SkQr0&qid=1661871307&sr=1-2-fd947bf3-57d2-4cc9-939d-2805f92cef28&linkCode=sl1&tag=abregepartena-21&linkId=6d1c643d708626ab5a2f56e5aabcfe31&language=fr_FR&ref_=as_li_ss_tl&th=1) depending on the number of drawer needed (3.72~7€/unit)

You could also sometime need manual drawer to store frequently used item, in that case we advise using these [latch](https://www.amazon.fr/gp/product/B09Q188H73?ie=UTF8&psc=1&linkCode=sl1&tag=abregepartena-21&linkId=6a57419938f0042e1b6d9fb142a8b039&language=fr_FR&ref_=as_li_ss_tl) they are cheap and effective. (1.7€/unit)

base on a 5 drawers rack the storage will cost around **4.25€** per drawer (2018), it is recommended to get as many drawer as your number of references

In our case we have around 320 references of piece the total cost was 985.73€ instead of the 1360€, it is mainly because all the piece was not stored in the storageman as they were to big to fit. the other reason is that we used a great approximation of 50cent per Drawer of Materials to be sure to get enougth budget.

### 3D model
in the [harware folder](./hwp) you can found all the related 3D parts to print

```
Note that the spring was not mention in the above section, as we tried to create PLA spring 
without great success. 
We recommand to use normal spring when the object are heavy 
```

## Building the Hardware

In this section we will explain how to build and assemble the Hardware section




# ===========================================================
Great — you chose **5: Add more modules**.
Here are **advanced, professional-grade modules** you can add to your Chemical Storage Manager web app.
I will design each module and give you backend + frontend structure and data models.

If you want full code for any module, just tell me the module number.

---

# 🧩 **ADVANCED MODULES YOU CAN ADD**

## **1. SDS (Safety Data Sheet) Viewer + Auto-Extraction**

This module lets you upload SDS PDF files and automatically extract:

* GHS pictograms
* Hazard statements (H-codes)
* Precautionary statements (P-codes)
* First aid measures
* Storage guidance
* Disposal guidance

### Database

```
SDS_DOCUMENT (
  id PK,
  product_id FK,
  file_path,
  version,
  issued_date,
  parsed boolean
)

SDS_SECTION (
  id PK,
  sds_id FK,
  section_number,
  title,
  content TEXT
)
```

### Backend flow

1. Upload PDF → Save file
2. Use **Apache Tika** or **PDFBox** to extract text
3. Identify sections like "Section 7: Handling & Storage"
4. Store extracted sections in DB
5. Link SDS to chemical product

### Frontend

* PDF preview
* Display extracted sections
* Warnings on dashboard if SDS is missing or outdated

---

## **2. Supplier & Purchase Order Management**

Track chemical purchases.

### Database

```
PURCHASE_ORDER (
  id PK,
  supplier_id FK,
  order_date,
  expected_arrival,
  status,
  total_cost
)

PURCHASE_ITEM (
  id PK,
  order_id FK,
  product_id FK,
  amount,
  unit,
  price_per_unit
)
```

### Features

* Purchase dashboard
* Auto-add to inventory when received
* Cost tracking
* Export as PDF

---

## **3. Sensor Integration (Temperature / Humidity)**

For advanced chemical storage safety.

### Supported devices

* ESP32 sensor
* Raspberry Pi temperature monitor
* Bluetooth IoT sensors

### Backend

Store real-time readings:

```
SENSOR_READING (
  id PK,
  location_id FK,
  temperature,
  humidity,
  timestamp
)
```

### Alerts

* Temperature outside safe range → WebSocket alert
* Log stored for historical charts

### Frontend

* Live charts (Realtime Line Chart)
* Color-coded locations (safe / warning / danger)

---

## **4. User Roles / Permission System**

Typical roles:

* **Admin**
* **Safety Officer**
* **Lab Technician**
* **Viewer**

### Permissions

| Feature            | Admin | Safety Officer | Tech | Viewer |
| ------------------ | ----- | -------------- | ---- | ------ |
| Add/edit chemicals | ✔     | ✔              | ✔    | ❌      |
| Move stock         | ✔     | ✔              | ✔    | ❌      |
| View SDS           | ✔     | ✔              | ✔    | ✔      |
| Manage users       | ✔     | ❌              | ❌    | ❌      |
| Approve disposal   | ✔     | ✔              | ❌    | ❌      |

### Backend

Spring Security with **method-level permissions**:

```java
@PreAuthorize("hasRole('ADMIN') or hasRole('SAFETY')")
public ChemicalProduct create(ChemicalProduct p) { ... }
```

---

## **5. Disposal & Waste Tracking**

Track chemicals that need disposal.

### Database

```
DISPOSAL_REQUEST (
  id PK,
  product_id FK,
  amount,
  reason,
  requested_by FK,
  status,
  requested_date,
  approved_by FK,
  approved_date
)
```

### Workflow

* Technician submits disposal request
* Safety Officer or Admin approves
* Stock reduces accordingly
* Disposal log maintained for audit

---

## **6. Reporting & Analytics Module**

Generate PDF/Excel reports:

### Report types

* Inventory summary
* Expired items
* Low-stock items
* Monthly chemical usage
* Safety compliance report
* Storage incompatibility insights

### PDF Generation

Use **Apache PDFBox** or **iText**.

### Frontend

* Date range selector
* Export buttons
* Data tables

---

## **7. Backup & Data Export Module**

Automatic daily export of:

* DB dump
* SDS documents
* Inventory snapshots

Plus manual export for audit inspections.

---

## **8. Multi-location Support**

For labs with multiple buildings.

### DB Changes

```
SITE (
  id PK,
  name,
  address
)

LOCATION (
  id PK,
  site_id FK,
  room,
  cabinet,
  shelf
)
```

### Features

* Switch between sites
* Site-specific inventory
* Cross-site stock transfer

---

## **9. Notification Center**

Channels:

* Email
* WebSocket
* SMS (optional via Twilio)
* In-app notifications

### Stored notifications

```
NOTIFICATION (
  id PK,
  user_id FK,
  type,
  message,
  read boolean,
  created_at
)
```

---

## **10. Machine Learning: Hazard Prediction (Optional & fun!)**

Use ML to predict hazard classifications based on:

* CAS number
* Keywords in SDS
* Structural alerts

This can be a future upgrade.

---

# ⭐ What next?

Choose one of the following:

### **A — Generate FULL BACKEND CODE for all selected modules**

(Spring Boot project with all files)

### **B — Generate FULL FRONTEND CODE**

(React with pages, components, routing)

### **C — Create full GitHub-ready project structure with all modules included**

### **D — Select specific modules to implement first**

Tell me **A, B, C, or D** — and I will generate the full implementation.
