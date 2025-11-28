package com.anode.storage.entity.core;

import java.time.LocalDate;

import com.anode.storage.entity.core.specification.ChemicalSpecification;
import com.anode.storage.entity.core.specification.HardwareSpecification;
import com.anode.storage.entity.location.Location;
import com.anode.storage.entity.safety.HazardClass;

/**
 * Builder for creating StorageItem instances with proper composition
 */
public class StorageItemBuilder {

    private final StorageItem item;

    private StorageItemBuilder(ProductType productType) {
        this.item = new StorageItem();
        this.item.setProductType(productType);
        this.item.setInventoryInfo(new InventoryInfo());
    }

    public static StorageItemBuilder chemical(String name) {
        StorageItemBuilder builder = new StorageItemBuilder(ProductType.CHEMICAL);
        builder.item.setName(name);
        builder.item.setChemicalSpec(new ChemicalSpecification());
        return builder;
    }

    public static StorageItemBuilder hardware(String name) {
        StorageItemBuilder builder = new StorageItemBuilder(ProductType.HARDWARE);
        builder.item.setName(name);
        builder.item.setHardwareSpec(new HardwareSpecification());
        return builder;
    }

    public static StorageItemBuilder electrical(String name) {
        StorageItemBuilder builder = new StorageItemBuilder(ProductType.ELECTRICAL);
        builder.item.setName(name);
        builder.item.setHardwareSpec(new HardwareSpecification());
        return builder;
    }

    public static StorageItemBuilder mechanical(String name) {
        StorageItemBuilder builder = new StorageItemBuilder(ProductType.MECHANICAL);
        builder.item.setName(name);
        builder.item.setHardwareSpec(new HardwareSpecification());
        return builder;
    }

    public static StorageItemBuilder electronic(String name) {
        StorageItemBuilder builder = new StorageItemBuilder(ProductType.ELECTRONIC);
        builder.item.setName(name);
        builder.item.setHardwareSpec(new HardwareSpecification());
        return builder;
    }

    public static StorageItemBuilder tool(String name) {
        StorageItemBuilder builder = new StorageItemBuilder(ProductType.TOOL);
        builder.item.setName(name);
        builder.item.setHardwareSpec(new HardwareSpecification());
        return builder;
    }

    // ========== COMMON FIELDS ==========

    public StorageItemBuilder description(String description) {
        item.setDescription(description);
        return this;
    }

    public StorageItemBuilder location(Location location) {
        item.setLocation(location);
        return this;
    }

    public StorageItemBuilder supplier(Supplier supplier) {
        item.setSupplier(supplier);
        return this;
    }

    // ========== INVENTORY INFO ==========

    public StorageItemBuilder quantity(Double quantity, String unit) {
        item.getInventoryInfo().setQuantity(quantity);
        item.getInventoryInfo().setUnit(unit);
        return this;
    }

    public StorageItemBuilder stockLimits(Double minStock, Double maxStock) {
        item.getInventoryInfo().setMinStock(minStock);
        item.getInventoryInfo().setMaxStock(maxStock);
        return this;
    }

    public StorageItemBuilder expirationDate(LocalDate expirationDate) {
        item.getInventoryInfo().setExpirationDate(expirationDate);
        return this;
    }

    // ========== CHEMICAL-SPECIFIC ==========

    public StorageItemBuilder casNumber(String casNumber) {
        if (item.getChemicalSpec() != null) {
            item.getChemicalSpec().setCasNumber(casNumber);
        }
        return this;
    }

    public StorageItemBuilder formula(String formula) {
        if (item.getChemicalSpec() != null) {
            item.getChemicalSpec().setFormula(formula);
        }
        return this;
    }

    public StorageItemBuilder hazardClass(HazardClass hazardClass) {
        item.setHazardClass(hazardClass);
        return this;
    }

    // ========== HARDWARE-SPECIFIC ==========

    public StorageItemBuilder manufacturer(String manufacturer) {
        if (item.getHardwareSpec() != null) {
            item.getHardwareSpec().setManufacturer(manufacturer);
        }
        return this;
    }

    public StorageItemBuilder modelNumber(String modelNumber) {
        if (item.getHardwareSpec() != null) {
            item.getHardwareSpec().setModelNumber(modelNumber);
        }
        return this;
    }

    public StorageItemBuilder serialNumber(String serialNumber) {
        if (item.getHardwareSpec() != null) {
            item.getHardwareSpec().setSerialNumber(serialNumber);
        }
        return this;
    }

    public StorageItemBuilder specifications(String specifications) {
        if (item.getHardwareSpec() != null) {
            item.getHardwareSpec().setSpecifications(specifications);
        }
        return this;
    }

    public StorageItem build() {
        return item;
    }
}
