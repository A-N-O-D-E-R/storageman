package com.anode.storage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anode.storage.entity.StockLog;
import com.anode.storage.repository.StockLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final StockLogRepository logRepo;

    @Transactional
    public void moveProduct(Long productId, Long fromLoc, Long toLoc, double amount, Long userId) {
        // update quantities...
        logRepo.save(new StockLog(productId, fromLoc, toLoc, amount, userId));
    }
}
