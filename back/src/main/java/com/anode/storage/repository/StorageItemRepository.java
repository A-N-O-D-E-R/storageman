package com.anode.storage.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.core.ProductType;
import com.anode.storage.entity.core.StorageItem;

public interface StorageItemRepository extends JpaRepository<StorageItem, Long> {
    List<StorageItem> findByExpirationDateBetween(LocalDate start, LocalDate end);

    List<StorageItem> findByProductType(ProductType productType);

    List<StorageItem> findByProductTypeAndExpirationDateBetween(
        ProductType productType, LocalDate start, LocalDate end);

    List<StorageItem> findByLocationId(Long locationId);
}
