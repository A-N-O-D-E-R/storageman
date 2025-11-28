package com.anode.storage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.anode.storage.entity.core.ProductType;
import com.anode.storage.entity.core.StorageItem;
import com.anode.storage.repository.StorageItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageItemService {

    private final StorageItemRepository repository;

    public List<StorageItem> findAll() {
        return repository.findAll();
    }

    public List<StorageItem> findByProductType(ProductType productType) {
        return repository.findByProductType(productType);
    }

    public StorageItem find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Storage item not found: " + id));
    }

    public StorageItem create(StorageItem item) {
        return repository.save(item);
    }

    public StorageItem update(Long id, StorageItem item) {
        StorageItem existing = find(id);
        item.setId(existing.getId());
        return repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * Check if item is at the specified location
     * Uses composition - accesses location through relationship
     */
    public String checkItemLocation(Long itemId, Long locationId) {
        StorageItem item = find(itemId);
        if (item.getLocation() != null && item.getLocation().getId().equals(locationId)) {
            return "Item is at the correct location";
        }
        return "Item is NOT at this location";
    }

    /**
     * Find all items with low stock
     * Uses business logic encapsulated in StorageItem
     */
    public List<StorageItem> findLowStockItems() {
        return repository.findAll().stream()
                .filter(StorageItem::isLowStock)
                .collect(Collectors.toList());
    }

    /**
     * Find all expired items
     * Uses business logic from InventoryInfo composition
     */
    public List<StorageItem> findExpiredItems() {
        return repository.findAll().stream()
                .filter(StorageItem::isExpired)
                .collect(Collectors.toList());
    }

    /**
     * Find items expiring soon
     * Demonstrates composition - logic delegated to InventoryInfo
     */
    public List<StorageItem> findItemsExpiringSoon(int days) {
        return repository.findAll().stream()
                .filter(item -> item.isExpiringSoon(days))
                .collect(Collectors.toList());
    }

    /**
     * Add quantity to an item
     * Uses InventoryInfo composition methods
     */
    public void addStock(Long itemId, double amount) {
        StorageItem item = find(itemId);
        item.getInventoryInfo().addQuantity(amount);
        repository.save(item);
    }

    /**
     * Remove quantity from an item
     * Uses InventoryInfo composition methods
     */
    public void removeStock(Long itemId, double amount) {
        StorageItem item = find(itemId);
        item.getInventoryInfo().removeQuantity(amount);
        repository.save(item);
    }
}
