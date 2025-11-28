-- ===========================
-- MIGRATION: Add support for composition pattern
-- ===========================
-- This migration ensures all columns exist for the embedded objects
-- JPA will map these automatically based on @Embedded annotations

-- Note: Most columns already exist from previous migrations
-- This migration adds any missing columns and documents the structure

-- Ensure all embedded InventoryInfo columns exist
-- (quantity, unit, minStock, maxStock, expirationDate already exist)

-- Ensure all embedded ChemicalSpecification columns exist
ALTER TABLE storage_items
ADD COLUMN IF NOT EXISTS cas_number VARCHAR(50),
ADD COLUMN IF NOT EXISTS formula VARCHAR(255);

-- Ensure all embedded HardwareSpecification columns exist
ALTER TABLE storage_items
ADD COLUMN IF NOT EXISTS manufacturer VARCHAR(255),
ADD COLUMN IF NOT EXISTS model_number VARCHAR(100),
ADD COLUMN IF NOT EXISTS serial_number VARCHAR(100),
ADD COLUMN IF NOT EXISTS specifications TEXT;

-- Add comment to document the composition structure
COMMENT ON TABLE storage_items IS 'Storage items with composition pattern: uses embedded InventoryInfo, ChemicalSpecification, and HardwareSpecification objects';

COMMENT ON COLUMN storage_items.product_type IS 'Determines which embedded specification object is used (CHEMICAL uses chemicalSpec, hardware types use hardwareSpec)';

-- Create indexes for commonly queried embedded fields
CREATE INDEX IF NOT EXISTS idx_storage_items_cas_number ON storage_items(cas_number);
CREATE INDEX IF NOT EXISTS idx_storage_items_model_number ON storage_items(model_number);
CREATE INDEX IF NOT EXISTS idx_storage_items_manufacturer ON storage_items(manufacturer);
