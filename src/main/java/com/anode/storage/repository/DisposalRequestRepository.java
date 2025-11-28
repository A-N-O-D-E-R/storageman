package com.anode.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.safety.DisposalRequest;

public interface DisposalRequestRepository extends JpaRepository<DisposalRequest, Long> {
    List<DisposalRequest> findByStatus(String status);
    List<DisposalRequest> findByRequestedById(Long userId);
    List<DisposalRequest> findByProductId(Long productId);
}
