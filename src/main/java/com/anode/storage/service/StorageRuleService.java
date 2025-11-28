package com.anode.storage.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.anode.storage.entity.safety.IncompatibilityRule;
import com.anode.storage.repository.IncompatibilityRuleRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for checking storage compatibility rules.
 * Currently handles hazard class incompatibility for chemicals.
 * Can be extended to support other storage rule types for hardware, electrical items, etc.
 */
@Service
@RequiredArgsConstructor
public class StorageRuleService {

    private final IncompatibilityRuleRepository rules;

    /**
     * Check if two hazard classes are incompatible.
     * Primarily used for chemical storage but can be adapted for other safety classifications.
     */
    public Optional<String> checkCompatibility(int hazardA, int hazardB) {
        return rules.findRule(hazardA, hazardB)
                    .map(IncompatibilityRule::getReason);
    }
}
