package com.anode.storage.entity.core;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Value object for batch/lot-specific information
 * Represents a specific receipt or production run of an item
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchInfo {

    /**
     * Internal batch number for tracking
     */
    private String batchNumber;

    /**
     * Supplier's lot number
     */
    private String lotNumber;

    /**
     * Date this batch was received
     */
    private LocalDate receivedDate;

    /**
     * Date this batch expires (can override reference default)
     */
    private LocalDate expirationDate;

    /**
     * Manufacturing date
     */
    private LocalDate manufactureDate;

    /**
     * Purchase order reference
     */
    private String purchaseOrderRef;

    /**
     * Check if batch is expired
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    /**
     * Check if batch is expiring soon
     */
    public boolean isExpiringSoon(int days) {
        if (expirationDate == null) {
            return false;
        }
        LocalDate threshold = LocalDate.now().plusDays(days);
        return expirationDate.isBefore(threshold) && !isExpired();
    }

    /**
     * Get age of batch in days since received
     */
    public long getAgeInDays() {
        if (receivedDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(receivedDate, LocalDate.now());
    }
}
