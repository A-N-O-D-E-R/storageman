package com.anode.storage.entity.core.specification;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Chemical-specific properties embedded in StorageItem
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChemicalSpecification {

    private String casNumber;
    private String formula;

    // Note: HazardClass relationship is handled in StorageItem for proper JPA mapping
}
