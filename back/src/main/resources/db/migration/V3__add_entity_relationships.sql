-- ===========================
-- MIGRATION: Add proper foreign key columns and fix references
-- ===========================

-- Add supplier_id to storage_items (if not exists from V1 schema)
ALTER TABLE storage_items
ADD COLUMN IF NOT EXISTS supplier_id INTEGER REFERENCES supplier(id);

-- Add hazard_class_id to storage_items (if not exists from V1 schema)
ALTER TABLE storage_items
ADD COLUMN IF NOT EXISTS hazard_class_id INTEGER REFERENCES hazard_class(id);

-- Update stock_log table name if it's still stock_logs
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'stock_logs') THEN
        ALTER TABLE stock_logs RENAME TO stock_log;
    END IF;
END $$;

-- Update incompatibility_rule table name if it's still incompatibility_rules
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'incompatibility_rules') THEN
        ALTER TABLE incompatibility_rules RENAME TO incompatibility_rule;
    END IF;
END $$;

-- Add indexes for better query performance on foreign keys
CREATE INDEX IF NOT EXISTS idx_storage_items_supplier_id ON storage_items(supplier_id);
CREATE INDEX IF NOT EXISTS idx_storage_items_hazard_class_id ON storage_items(hazard_class_id);
CREATE INDEX IF NOT EXISTS idx_stock_log_product_id ON stock_log(product_id);
CREATE INDEX IF NOT EXISTS idx_stock_log_user_id ON stock_log(user_id);
CREATE INDEX IF NOT EXISTS idx_stock_log_from_location ON stock_log(from_location);
CREATE INDEX IF NOT EXISTS idx_stock_log_to_location ON stock_log(to_location);
CREATE INDEX IF NOT EXISTS idx_sds_document_product_id ON sds_document(product_id);
CREATE INDEX IF NOT EXISTS idx_purchase_item_product_id ON purchase_item(product_id);
CREATE INDEX IF NOT EXISTS idx_disposal_request_product_id ON disposal_request(product_id);
CREATE INDEX IF NOT EXISTS idx_notification_user_id ON notification(user_id);
CREATE INDEX IF NOT EXISTS idx_sensor_reading_location_id ON sensor_reading(location_id);
CREATE INDEX IF NOT EXISTS idx_location_site_id ON location(site_id);
