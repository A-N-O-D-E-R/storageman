package com.anode.storage.entity.core;

/**
 * Enum representing different types of storage items.
 */
public enum ProductType {
    CHEMICAL("CHEM", "Chemical Product"),
    HARDWARE("HW", "Hardware Component"),
    ELECTRICAL("ELEC", "Electrical Component"),
    MECHANICAL("MECH", "Mechanical Component"),
    ELECTRONIC("ELCTR", "Electronic Component"),
    TOOL("TOOL", "Tool"),
    OTHER("OTH", "Other");

    private final String prefix;
    private final String displayName;

    ProductType(String prefix, String displayName) {
        this.prefix = prefix;
        this.displayName = displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDisplayName() {
        return displayName;
    }
}
