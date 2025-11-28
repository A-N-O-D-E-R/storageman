package com.anode.storage.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.anode.storage.entity.ChemicalProduct;
import com.anode.storage.repository.ChemicalProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpirationService {

    private final ChemicalProductRepository repo;
    private final EmailService emailService;

    public List<ChemicalProduct> findExpiringSoon() {
        LocalDate now = LocalDate.now();
        LocalDate soon = now.plusDays(30);

        return repo.findByExpirationDateBetween(now, soon);
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void sendExpiringNotifications() {
        List<ChemicalProduct> expiring = findExpiringSoon();

        if (!expiring.isEmpty()) {
            emailService.sendExpirationWarning(expiring);
        }
    }
}