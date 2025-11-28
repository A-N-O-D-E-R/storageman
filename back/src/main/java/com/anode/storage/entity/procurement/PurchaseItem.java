package com.anode.storage.entity.procurement;

import java.math.BigDecimal;

import com.anode.storage.entity.core.StorageItem;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purchase_item")
@Getter
@Setter
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private PurchaseOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private StorageItem product;

    private Double amount;
    private String unit;
    private BigDecimal pricePerUnit;
}
