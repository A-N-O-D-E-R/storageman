package com.anode.storage.entity.core;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Value object for inventory tracking information
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryInfo {

    private Double quantity;
    private String unit;
    private Double minStock;
    private Double maxStock;
    private LocalDate expirationDate;

    /**
     * Check if stock is below minimum threshold
     */
    public boolean isLowStock() {
        return minStock != null && quantity != null && quantity < minStock;
    }

    /**
     * Check if item is expired
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    /**
     * Check if item is expiring soon (within days)
     */
    public boolean isExpiringSoon(int days) {
        if (expirationDate == null) {
            return false;
        }
        LocalDate threshold = LocalDate.now().plusDays(days);
        return expirationDate.isBefore(threshold) && !isExpired();
    }

    /**
     * Add to quantity
     */
    public void addQuantity(double amount) {
        this.quantity = (this.quantity != null ? this.quantity : 0.0) + amount;
    }

    /**
     * Remove from quantity
     */
    public void removeQuantity(double amount) {
        this.quantity = (this.quantity != null ? this.quantity : 0.0) - amount;
        if (this.quantity < 0) {
            this.quantity = 0.0;
        }
    }
}
