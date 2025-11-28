package com.anode.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anode.storage.entity.procurement.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    List<PurchaseOrder> findByStatus(String status);
}
