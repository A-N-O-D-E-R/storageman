package com.anode.storage.entity.location;

import java.util.ArrayList;
import java.util.List;

import com.anode.storage.entity.core.StorageItem;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "location")
@Getter
@Setter
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    private String room;
    private String cabinet;
    private String shelf;
    private String temperatureRange;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<StorageItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<SensorReading> sensorReadings = new ArrayList<>();
}
