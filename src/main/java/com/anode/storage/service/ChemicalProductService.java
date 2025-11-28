package com.anode.storage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.anode.storage.entity.ChemicalProduct;
import com.anode.storage.repository.ChemicalProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChemicalProductService {

    private final ChemicalProductRepository repository;

    public List<ChemicalProduct> findAll() {
        return repository.findAll();
    }

    public ChemicalProduct find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public ChemicalProduct create(ChemicalProduct product) {
        return repository.save(product);
    }

    public ChemicalProduct update(Long id, ChemicalProduct product) {
        ChemicalProduct existing = find(id);
        product.setId(existing.getId());
        return repository.save(product);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public String checkProductLocation(Long productId, Long locationId) {
        ChemicalProduct product = find(productId);
        if (product.getLocationId().equals(locationId)) {
            return "Product is at the correct location";
        }
        return "Product is NOT at this location";
    }
}
