package com.anode.storage.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.anode.storage.entity.core.ProductType;
import com.anode.storage.entity.core.StorageItem;
import com.anode.storage.repository.StorageItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpirationService {

    private final StorageItemRepository repo;
    private final EmailService emailService;

    public List<StorageItem> findExpiringSoon() {
        LocalDate now = LocalDate.now();
        LocalDate soon = now.plusDays(30);

        return repo.findByExpirationDateBetween(now, soon);
    }

    public List<StorageItem> findExpiringSoonByType(ProductType productType) {
        LocalDate now = LocalDate.now();
        LocalDate soon = now.plusDays(30);

        return repo.findByProductTypeAndExpirationDateBetween(productType, now, soon);
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void sendExpiringNotifications() {
        // Only check expiration for product types that can expire (chemicals, some hardware)
        List<StorageItem> expiring = findExpiringSoon();

        if (!expiring.isEmpty()) {
            emailService.sendExpirationWarning(expiring);
        }
    }
}