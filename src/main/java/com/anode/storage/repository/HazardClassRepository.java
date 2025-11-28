package com.anode.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.safety.HazardClass;

public interface HazardClassRepository extends JpaRepository<HazardClass, Long> {
    HazardClass findByGhsCode(String ghsCode);
    HazardClass findByName(String name);
}
