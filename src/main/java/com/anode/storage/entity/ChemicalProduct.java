package com.anode.storage.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chemical_products")
@Getter
@Setter
public class ChemicalProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String casNumber;
    private String formula;
    private String supplier;

    private Double quantity;
    private String unit;

    private LocalDate expirationDate;
    private Integer hazardClass;

    @Column(name = "location_id")
    private Long locationId;

    private Double minStock;
    private Double maxStock;
}
