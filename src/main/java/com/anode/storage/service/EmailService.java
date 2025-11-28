package com.anode.storage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.anode.storage.entity.ChemicalProduct;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    public void sendExpirationWarning(List<ChemicalProduct> products) {
        log.info("Sending expiration warning for {} products", products.size());
        // Email sending logic would go here
        for (ChemicalProduct product : products) {
            log.info("Product {} expires on {}", product.getName(), product.getExpirationDate());
        }
    }
}
