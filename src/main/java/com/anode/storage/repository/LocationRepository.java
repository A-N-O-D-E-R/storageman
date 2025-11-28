package com.anode.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.location.Location;



public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findBySiteId(Long siteId);
    List<Location> findByRoom(String room);
}
