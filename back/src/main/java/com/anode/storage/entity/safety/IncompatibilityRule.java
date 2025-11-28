package com.anode.storage.entity.safety;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "incompatibility_rule")
@Getter
@Setter
public class IncompatibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hazard_class_a")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private HazardClass hazardClassAEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hazard_class_b")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private HazardClass hazardClassBEntity;

    // Keep as Integer for backward compatibility
    @Column(name = "hazard_class_a", insertable = false, updatable = false)
    private Integer hazardClassA;

    @Column(name = "hazard_class_b", insertable = false, updatable = false)
    private Integer hazardClassB;

    @Column(columnDefinition = "TEXT")
    private String reason;
}
