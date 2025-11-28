-- ===========================
-- MIGRATION: Split StorageItem into StorageReference and StorageItem (batches)
-- ===========================

-- Create storage_references table (product master data)
CREATE TABLE IF NOT EXISTS storage_references (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    product_type VARCHAR(20) NOT NULL,
    description TEXT,
    sku VARCHAR(100) UNIQUE,

    -- Embedded ChemicalSpecification
    cas_number VARCHAR(50),
    formula VARCHAR(255),

    -- Embedded HardwareSpecification
    manufacturer VARCHAR(255),
    model_number VARCHAR(100),
    serial_number VARCHAR(100),
    specifications TEXT,

    -- Relationships
    supplier_id INTEGER REFERENCES supplier(id),
    hazard_class_id INTEGER REFERENCES hazard_class(id),

    -- Defaults
    default_min_stock DECIMAL,
    default_max_stock DECIMAL,
    default_unit VARCHAR(20),
    shelf_life_days INTEGER,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- Add reference_id to storage_items (batches now reference the master data)
ALTER TABLE storage_items
ADD COLUMN IF NOT EXISTS reference_id INTEGER REFERENCES storage_references(id);

-- Add batch-specific columns to storage_items
ALTER TABLE storage_items
ADD COLUMN IF NOT EXISTS batch_number VARCHAR(100),
ADD COLUMN IF NOT EXISTS lot_number VARCHAR(100),
ADD COLUMN IF NOT EXISTS received_date DATE,
ADD COLUMN IF NOT EXISTS manufacture_date DATE,
ADD COLUMN IF NOT EXISTS purchase_order_ref VARCHAR(100),
ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'AVAILABLE',
ADD COLUMN IF NOT EXISTS notes TEXT;

-- Rename expiration_date for clarity (it's batch-specific now)
-- Already exists from InventoryInfo

-- Update SDS documents to reference storage_references instead of storage_items
ALTER TABLE sds_document
ADD COLUMN IF NOT EXISTS reference_id INTEGER REFERENCES storage_references(id);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_storage_items_reference_id ON storage_items(reference_id);
CREATE INDEX IF NOT EXISTS idx_storage_items_batch_number ON storage_items(batch_number);
CREATE INDEX IF NOT EXISTS idx_storage_items_status ON storage_items(status);
CREATE INDEX IF NOT EXISTS idx_storage_items_received_date ON storage_items(received_date);
CREATE INDEX IF NOT EXISTS idx_storage_references_sku ON storage_references(sku);
CREATE INDEX IF NOT EXISTS idx_storage_references_product_type ON storage_references(product_type);
CREATE INDEX IF NOT EXISTS idx_storage_references_active ON storage_references(active);
CREATE INDEX IF NOT EXISTS idx_sds_document_reference_id ON sds_document(reference_id);

-- Add comments
COMMENT ON TABLE storage_references IS 'Product master data (immutable catalog entries)';
COMMENT ON TABLE storage_items IS 'Individual batches/lots of products (mutable inventory)';

COMMENT ON COLUMN storage_items.reference_id IS 'Links to the product master data';
COMMENT ON COLUMN storage_items.batch_number IS 'Internal batch tracking number';
COMMENT ON COLUMN storage_items.lot_number IS 'Supplier lot number';
COMMENT ON COLUMN storage_items.status IS 'AVAILABLE, RESERVED, IN_USE, QUARANTINED, EXPIRED, DISPOSED, EMPTY';
