package com.anode.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.safety.SdsDocument;


public interface SdsDocumentRepository extends JpaRepository<SdsDocument, Long> {
    List<SdsDocument> findByProductId(Long productId);
    List<SdsDocument> findByParsed(Boolean parsed);
}
