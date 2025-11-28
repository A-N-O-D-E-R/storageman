package com.anode.storage.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.location.SensorReading;


public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    List<SensorReading> findByLocationId(Long locationId);
    List<SensorReading> findByLocationIdAndTimestampBetween(
        Long locationId, LocalDateTime start, LocalDateTime end);
}
