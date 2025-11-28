package com.anode.storage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anode.storage.entity.core.ProductType;
import com.anode.storage.entity.core.StorageReference;

public interface StorageReferenceRepository extends JpaRepository<StorageReference, Long> {

    List<StorageReference> findByProductType(ProductType productType);

    List<StorageReference> findByActive(Boolean active);

    Optional<StorageReference> findBySku(String sku);

    List<StorageReference> findByNameContainingIgnoreCase(String name);

    List<StorageReference> findBySupplierId(Long supplierId);

    @Query("SELECT r FROM StorageReference r WHERE r.active = true")
    List<StorageReference> findAllActive();

    @Query("SELECT r FROM StorageReference r WHERE SIZE(r.items) > 0")
    List<StorageReference> findAllWithItems();

    @Query("SELECT r FROM StorageReference r WHERE r.productType = ?1 AND r.active = true")
    List<StorageReference> findActiveByType(ProductType productType);
}
