package com.anode.storage.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.anode.storage.entity.IncompatibilityRule;
import com.anode.storage.repository.IncompatibilityRuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageRuleService {

    private final IncompatibilityRuleRepository rules;

    public Optional<String> checkCompatibility(int hazardA, int hazardB) {
        return rules.findRule(hazardA, hazardB)
                    .map(IncompatibilityRule::getReason);
    }
}
