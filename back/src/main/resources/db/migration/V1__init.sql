-- ===========================
-- HAZARD CLASSES
-- ===========================
CREATE TABLE hazard_class (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  ghs_code VARCHAR(50),
  description TEXT
);

-- ===========================
-- SUPPLIERS
-- ===========================
CREATE TABLE supplier (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255),
  contact VARCHAR(255),
  phone VARCHAR(50)
);

-- ===========================
-- SITES (multi-location support)
-- ===========================
CREATE TABLE site (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  address TEXT
);

-- ===========================
-- STORAGE LOCATIONS
-- ===========================
CREATE TABLE location (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  site_id INTEGER REFERENCES site(id) ON DELETE SET NULL,
  room VARCHAR(50),
  cabinet VARCHAR(50),
  shelf VARCHAR(50),
  temperature_range VARCHAR(50)
);

-- ===========================
-- CHEMICAL PRODUCTS
-- ===========================
CREATE TABLE chemical_product (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  cas_number VARCHAR(50),
  hazard_class_id INTEGER REFERENCES hazard_class(id),
  supplier_id INTEGER REFERENCES supplier(id),
  quantity DECIMAL,
  unit VARCHAR(20),
  expiration_date DATE,
  sds_url VARCHAR(255),
  min_quantity DECIMAL DEFAULT 0,
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

-- ===========================
-- USERS
-- ===========================
CREATE TABLE users (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL
);

-- ===========================
-- STORAGE INCOMPATIBILITY RULES
-- ===========================
CREATE TABLE incompatibility_rule (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  hazard_class_a INTEGER REFERENCES hazard_class(id),
  hazard_class_b INTEGER REFERENCES hazard_class(id),
  reason TEXT
);

-- ===========================
-- STOCK MOVEMENT LOG
-- ===========================
CREATE TABLE stock_log (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  product_id INTEGER REFERENCES chemical_product(id),
  from_location INTEGER REFERENCES location(id),
  to_location INTEGER REFERENCES location(id),
  amount DECIMAL,
  timestamp TIMESTAMP DEFAULT now(),
  user_id INTEGER REFERENCES users(id),
  note TEXT
);

-- ===========================
-- SDS DOCUMENTS
-- ===========================
CREATE TABLE sds_document (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  product_id INTEGER REFERENCES chemical_product(id),
  file_path VARCHAR(255),
  version VARCHAR(50),
  issued_date DATE,
  parsed BOOLEAN DEFAULT FALSE
);

-- ===========================
-- SDS SECTIONS (automatically extracted)
-- ===========================
CREATE TABLE sds_section (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  sds_id INTEGER REFERENCES sds_document(id),
  section_number INTEGER,
  title VARCHAR(255),
  content TEXT
);

-- ===========================
-- PURCHASE ORDERS
-- ===========================
CREATE TABLE purchase_order (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  supplier_id INTEGER REFERENCES supplier(id),
  order_date DATE,
  expected_arrival DATE,
  status VARCHAR(50),
  total_cost DECIMAL
);

-- ===========================
-- PURCHASE ORDER ITEMS
-- ===========================
CREATE TABLE purchase_item (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  order_id INTEGER REFERENCES purchase_order(id) ON DELETE CASCADE,
  product_id INTEGER REFERENCES chemical_product(id),
  amount DECIMAL,
  unit VARCHAR(20),
  price_per_unit DECIMAL
);

-- ===========================
-- SENSOR READINGS
-- ===========================
CREATE TABLE sensor_reading (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  location_id INTEGER REFERENCES location(id),
  temperature DECIMAL,
  humidity DECIMAL,
  timestamp TIMESTAMP DEFAULT now()
);

-- ===========================
-- DISPOSAL REQUESTS
-- ===========================
CREATE TABLE disposal_request (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  product_id INTEGER REFERENCES chemical_product(id),
  amount DECIMAL,
  reason TEXT,
  requested_by INTEGER REFERENCES users(id),
  status VARCHAR(50),
  requested_date TIMESTAMP DEFAULT now(),
  approved_by INTEGER REFERENCES users(id),
  approved_date TIMESTAMP
);

-- ===========================
-- NOTIFICATION CENTER
-- ===========================
CREATE TABLE notification (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id INTEGER REFERENCES users(id),
  type VARCHAR(50),
  message TEXT,
  read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT now()
);
