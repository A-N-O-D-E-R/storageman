package com.anode.storage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.anode.storage.entity.core.StorageItem;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    public void sendExpirationWarning(List<StorageItem> items) {
        log.info("Sending expiration warning for {} items", items.size());
        // Email sending logic would go here
        for (StorageItem item : items) {
            log.info("Item {} ({}) expires on {}",
                item.getName(),
                item.getProductType().getDisplayName(),
                item.getExpirationDate());
        }
    }
}
