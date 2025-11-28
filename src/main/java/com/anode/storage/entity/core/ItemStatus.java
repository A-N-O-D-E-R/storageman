package com.anode.storage.entity.core;

/**
 * Status of a storage item batch
 */
public enum ItemStatus {
    /**
     * Available for use
     */
    AVAILABLE,

    /**
     * Reserved for a specific purpose/project
     */
    RESERVED,

    /**
     * In use/checked out
     */
    IN_USE,

    /**
     * Quarantined (quality issues, waiting for testing)
     */
    QUARANTINED,

    /**
     * Expired but not yet disposed
     */
    EXPIRED,

    /**
     * Marked for disposal
     */
    DISPOSED,

    /**
     * Completely depleted
     */
    EMPTY
}
