package com.anode.storage.entity.safety;

import java.util.ArrayList;
import java.util.List;

import com.anode.storage.entity.core.StorageItem;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hazard_class")
@Getter
@Setter
public class HazardClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String ghsCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "hazardClassEntity", cascade = CascadeType.ALL)
    private List<StorageItem> items = new ArrayList<>();
}
