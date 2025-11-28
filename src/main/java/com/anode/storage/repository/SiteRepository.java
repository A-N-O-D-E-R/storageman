package com.anode.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.location.Site;

public interface SiteRepository extends JpaRepository<Site, Long> {
    Site findByName(String name);
}
