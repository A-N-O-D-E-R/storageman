package com.anode.storage.entity.core;

import java.util.ArrayList;
import java.util.List;

import com.anode.storage.entity.core.specification.ChemicalSpecification;
import com.anode.storage.entity.core.specification.HardwareSpecification;
import com.anode.storage.entity.safety.HazardClass;
import com.anode.storage.entity.safety.SdsDocument;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * StorageReference represents the immutable master data for a product/item.
 * This is the "catalog" entry - what the item IS, not where/how much you have.
 *
 * Examples:
 * - "Ethanol 99.9% ACS Grade" (chemical)
 * - "Arduino Uno R3" (hardware)
 * - "Resistor 10kΩ 1/4W" (electrical)
 *
 * Each reference can have multiple StorageItem batches/lots.
 */
@Entity
@Table(name = "storage_references")
@Getter
@Setter
public class StorageReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Product name/description
     */
    @Column(nullable = false)
    private String name;

    /**
     * Type of product (CHEMICAL, HARDWARE, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    /**
     * Detailed description
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * SKU or internal catalog number
     */
    @Column(unique = true)
    private String sku;

    // ========== COMPOSITION: Embedded Specifications ==========

    /**
     * Chemical-specific properties (immutable)
     */
    @Embedded
    private ChemicalSpecification chemicalSpec;

    /**
     * Hardware-specific properties (immutable)
     */
    @Embedded
    private HardwareSpecification hardwareSpec;

    // ========== IMMUTABLE RELATIONSHIPS ==========

    /**
     * Primary/preferred supplier for this product
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "items", "purchaseOrders"})
    private Supplier supplier;

    /**
     * Hazard classification (for chemicals)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hazard_class_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "items"})
    private HazardClass hazardClass;

    // ========== INVENTORY DEFAULTS ==========

    /**
     * Default minimum stock level (across all batches)
     */
    private Double defaultMinStock;

    /**
     * Default maximum stock level (across all batches)
     */
    private Double defaultMaxStock;

    /**
     * Default unit of measure
     */
    private String defaultUnit;

    /**
     * Shelf life in days (for items with expiration)
     */
    private Integer shelfLifeDays;

    /**
     * Is this reference active/orderable?
     */
    @Column(nullable = false)
    private Boolean active = true;

    // ========== RELATIONSHIPS ==========

    /**
     * All batches/lots of this product
     */
    @OneToMany(mappedBy = "reference", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("reference")
    private List<StorageItem> items = new ArrayList<>();

    /**
     * SDS documents for this product
     */
    @OneToMany(mappedBy = "reference", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("reference")
    private List<SdsDocument> sdsDocuments = new ArrayList<>();

    // ========== BUSINESS LOGIC ==========

    /**
     * Get total quantity across all batches
     */
    public double getTotalQuantity() {
        return items.stream()
            .filter(item -> item.getStatus() == ItemStatus.AVAILABLE
                         || item.getStatus() == ItemStatus.RESERVED)
            .mapToDouble(item -> item.getInventoryInfo().getQuantity())
            .sum();
    }

    /**
     * Get number of batches
     */
    public int getBatchCount() {
        return items.size();
    }

    /**
     * Get number of available batches
     */
    public int getAvailableBatchCount() {
        return (int) items.stream()
            .filter(item -> item.getStatus() == ItemStatus.AVAILABLE)
            .count();
    }

    /**
     * Check if total stock is below minimum
     */
    public boolean isLowStock() {
        return defaultMinStock != null && getTotalQuantity() < defaultMinStock;
    }

    /**
     * Check if this is a chemical
     */
    public boolean isChemical() {
        return productType == ProductType.CHEMICAL;
    }

    /**
     * Check if this is hardware
     */
    public boolean isHardware() {
        return productType == ProductType.HARDWARE
            || productType == ProductType.ELECTRICAL
            || productType == ProductType.MECHANICAL
            || productType == ProductType.ELECTRONIC;
    }

    // ========== LIFECYCLE HOOKS ==========

    @PrePersist
    @PreUpdate
    protected void validateSpecifications() {
        if (isChemical() && chemicalSpec == null) {
            chemicalSpec = new ChemicalSpecification();
        }
        if (isHardware() && hardwareSpec == null) {
            hardwareSpec = new HardwareSpecification();
        }
    }
}
