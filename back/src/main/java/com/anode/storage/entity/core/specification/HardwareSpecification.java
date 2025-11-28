package com.anode.storage.entity.core.specification;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Hardware/Electronic/Mechanical-specific properties embedded in StorageItem
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HardwareSpecification {

    private String manufacturer;
    private String modelNumber;
    private String serialNumber;

    @Column(columnDefinition = "TEXT")
    private String specifications;
}
