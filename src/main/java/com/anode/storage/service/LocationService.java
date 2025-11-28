package com.anode.storage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anode.storage.entity.core.StorageItem;
import com.anode.storage.entity.core.User;
import com.anode.storage.entity.location.Location;
import com.anode.storage.entity.safety.StockLog;
import com.anode.storage.repository.LocationRepository;
import com.anode.storage.repository.StockLogRepository;
import com.anode.storage.repository.StorageItemRepository;
import com.anode.storage.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final StockLogRepository logRepo;
    private final StorageItemRepository itemRepo;
    private final LocationRepository locationRepo;
    private final UserRepository userRepo;

    @Transactional
    public void moveItem(Long itemId, Long fromLocId, Long toLocId, double amount, Long userId) {
        // Load entities
        StorageItem item = itemRepo.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        Location fromLoc = fromLocId != null ? locationRepo.findById(fromLocId).orElse(null) : null;
        Location toLoc = toLocId != null ? locationRepo.findById(toLocId).orElse(null) : null;
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Update item location
        item.setLocation(toLoc);
        itemRepo.save(item);

        // Create stock log using entity relationships
        StockLog log = new StockLog();
        log.setProduct(item);
        log.setFromLocation(fromLoc);
        log.setToLocation(toLoc);
        log.setAmount(amount);
        log.setUser(user);

        logRepo.save(log);
    }
}
