-- ===========================
-- MIGRATION: Refactor chemical_product to storage_items
-- ===========================

-- Rename chemical_product table to storage_items
ALTER TABLE chemical_product RENAME TO storage_items;

-- Add product_type column with default value for existing rows
ALTER TABLE storage_items
ADD COLUMN product_type VARCHAR(20) NOT NULL DEFAULT 'CHEMICAL';

-- Add new fields for hardware/electrical/mechanical items
ALTER TABLE storage_items
ADD COLUMN manufacturer VARCHAR(255),
ADD COLUMN model_number VARCHAR(100),
ADD COLUMN serial_number VARCHAR(100),
ADD COLUMN specifications TEXT,
ADD COLUMN description TEXT;

-- Rename formula column if it doesn't exist in your actual schema
-- (based on the Java entity, but not in the SQL schema)
ALTER TABLE storage_items
ADD COLUMN formula VARCHAR(255);

-- Update foreign key constraints in related tables
ALTER TABLE stock_log
DROP CONSTRAINT IF EXISTS stock_log_product_id_fkey,
ADD CONSTRAINT stock_log_product_id_fkey
  FOREIGN KEY (product_id) REFERENCES storage_items(id);

ALTER TABLE sds_document
DROP CONSTRAINT IF EXISTS sds_document_product_id_fkey,
ADD CONSTRAINT sds_document_product_id_fkey
  FOREIGN KEY (product_id) REFERENCES storage_items(id);

ALTER TABLE purchase_item
DROP CONSTRAINT IF EXISTS purchase_item_product_id_fkey,
ADD CONSTRAINT purchase_item_product_id_fkey
  FOREIGN KEY (product_id) REFERENCES storage_items(id);

ALTER TABLE disposal_request
DROP CONSTRAINT IF EXISTS disposal_request_product_id_fkey,
ADD CONSTRAINT disposal_request_product_id_fkey
  FOREIGN KEY (product_id) REFERENCES storage_items(id);

-- Add indexes for better query performance
CREATE INDEX idx_storage_items_product_type ON storage_items(product_type);
CREATE INDEX idx_storage_items_expiration_date ON storage_items(expiration_date);
CREATE INDEX idx_storage_items_location_id ON storage_items(location_id);
