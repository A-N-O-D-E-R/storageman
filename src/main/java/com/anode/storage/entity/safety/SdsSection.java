package com.anode.storage.entity.safety;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sds_section")
@Getter
@Setter
public class SdsSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sds_id")
    private SdsDocument sdsDocument;

    private Integer sectionNumber;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
}
