package com.anode.storage.entity.safety;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.anode.storage.entity.core.StorageReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sds_document")
@Getter
@Setter
public class SdsDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * SDS documents belong to the product reference (not individual batches)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id")
    private StorageReference reference;

    private String filePath;
    private String version;
    private LocalDate issuedDate;

    @Column(nullable = false)
    private Boolean parsed = false;

    @OneToMany(mappedBy = "sdsDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SdsSection> sections = new ArrayList<>();
}
