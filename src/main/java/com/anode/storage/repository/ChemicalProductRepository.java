package com.anode.storage.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.ChemicalProduct;

public interface ChemicalProductRepository extends JpaRepository<ChemicalProduct, Long> {
    List<ChemicalProduct> findByExpirationDateBetween(LocalDate start, LocalDate end);
}
