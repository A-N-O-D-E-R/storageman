package com.anode.storage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.anode.storage.entity.safety.IncompatibilityRule;

public interface IncompatibilityRuleRepository extends JpaRepository<IncompatibilityRule, Long> {

    @Query("SELECT r FROM IncompatibilityRule r WHERE " +
           "(r.hazardClassA = :hazardA AND r.hazardClassB = :hazardB) OR " +
           "(r.hazardClassA = :hazardB AND r.hazardClassB = :hazardA)")
    Optional<IncompatibilityRule> findRule(@Param("hazardA") int hazardA, @Param("hazardB") int hazardB);
}
