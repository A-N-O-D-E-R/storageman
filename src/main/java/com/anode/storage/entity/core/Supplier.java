package com.anode.storage.entity.core;

import java.util.ArrayList;
import java.util.List;

import com.anode.storage.entity.procurement.PurchaseOrder;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "supplier")
@Getter
@Setter
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contact;
    private String phone;

    @OneToMany(mappedBy = "supplierEntity", cascade = CascadeType.ALL)
    private List<StorageItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
}
