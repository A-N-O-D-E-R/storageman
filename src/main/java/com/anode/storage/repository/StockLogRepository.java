package com.anode.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.safety.StockLog;


public interface StockLogRepository extends JpaRepository<StockLog, Long> {
}
