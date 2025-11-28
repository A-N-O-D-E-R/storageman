package com.anode.storage.entity.core;

import java.util.ArrayList;
import java.util.List;

import com.anode.storage.entity.location.Location;
import com.anode.storage.entity.procurement.PurchaseItem;
import com.anode.storage.entity.safety.DisposalRequest;
import com.anode.storage.entity.safety.StockLog;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * StorageItem represents a specific batch/lot/instance of a product.
 * This is the physical inventory - WHERE it is, HOW MUCH you have, WHEN it expires.
 *
 * Examples:
 * - Batch #2024-001 of Ethanol, 5L, expires 2026-12-31, in cabinet A1
 * - Lot #ARD-2024-05 of Arduino Uno R3, 50 pieces, in storage room B
 *
 * Each item references a StorageReference (master product data).
 */
@Entity
@Table(name = "storage_items")
@Getter
@Setter
public class StorageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== REFERENCE TO MASTER DATA ==========

    /**
     * Reference to the product master data (immutable info)
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reference_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "items"})
    private StorageReference reference;

    // ========== BATCH/LOT INFORMATION ==========

    /**
     * Batch/lot specific information
     */
    @Embedded
    private BatchInfo batchInfo = new BatchInfo();

    /**
     * Current status of this batch
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.AVAILABLE;

    // ========== INVENTORY INFORMATION ==========

    /**
     * Inventory tracking for this specific batch
     */
    @Embedded
    private InventoryInfo inventoryInfo = new InventoryInfo();

    /**
     * Current storage location of this batch
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "items", "sensorReadings", "site"})
    private Location location;

    /**
     * Notes specific to this batch
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    // ========== AUDIT & TRACKING ==========

    /**
     * Stock movement history for this batch
     */
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("item")
    private List<StockLog> stockLogs = new ArrayList<>();

    /**
     * Purchase items referencing this batch
     */
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("item")
    private List<PurchaseItem> purchaseItems = new ArrayList<>();

    /**
     * Disposal requests for this batch
     */
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("item")
    private List<DisposalRequest> disposalRequests = new ArrayList<>();

    // ========== BUSINESS LOGIC ==========

    /**
     * Check if this batch is expired
     */
    public boolean isExpired() {
        return status == ItemStatus.EXPIRED
            || (batchInfo != null && batchInfo.isExpired());
    }

    /**
     * Check if this batch is expiring soon
     */
    public boolean isExpiringSoon(int days) {
        return batchInfo != null && batchInfo.isExpiringSoon(days);
    }

    /**
     * Check if this batch is available for use
     */
    public boolean isAvailable() {
        return status == ItemStatus.AVAILABLE && !isExpired();
    }

    /**
     * Check if quantity is low (compared to reference defaults)
     */
    public boolean isLowStock() {
        if (reference == null || reference.getDefaultMinStock() == null) {
            return false;
        }
        return inventoryInfo != null
            && inventoryInfo.getQuantity() != null
            && inventoryInfo.getQuantity() < reference.getDefaultMinStock();
    }

    /**
     * Get the product type from reference
     */
    public ProductType getProductType() {
        return reference != null ? reference.getProductType() : null;
    }

    /**
     * Get the product name from reference
     */
    public String getProductName() {
        return reference != null ? reference.getName() : null;
    }

    /**
     * Add quantity to this batch
     */
    public void addQuantity(double amount) {
        if (inventoryInfo != null) {
            inventoryInfo.addQuantity(amount);
        }
    }

    /**
     * Remove quantity from this batch
     */
    public void removeQuantity(double amount) {
        if (inventoryInfo != null) {
            inventoryInfo.removeQuantity(amount);
            // Auto-mark as empty if depleted
            if (inventoryInfo.getQuantity() <= 0) {
                this.status = ItemStatus.EMPTY;
            }
        }
    }

    /**
     * Mark this batch as expired
     */
    public void markExpired() {
        this.status = ItemStatus.EXPIRED;
    }

    // ========== LIFECYCLE HOOKS ==========

    @PrePersist
    @PreUpdate
    protected void validateBatch() {
        // Initialize batch info if null
        if (batchInfo == null) {
            batchInfo = new BatchInfo();
        }

        // Initialize inventory info if null
        if (inventoryInfo == null) {
            inventoryInfo = new InventoryInfo();
        }

        // Auto-update status based on expiration
        if (batchInfo.isExpired() && status == ItemStatus.AVAILABLE) {
            status = ItemStatus.EXPIRED;
        }
    }
}
