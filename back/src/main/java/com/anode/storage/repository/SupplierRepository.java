package com.anode.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.core.Supplier;


public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Supplier findByName(String name);
}
