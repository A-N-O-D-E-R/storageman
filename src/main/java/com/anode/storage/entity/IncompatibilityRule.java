package com.anode.storage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "incompatibility_rules")
@Getter
@Setter
public class IncompatibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer hazardClassA;
    private Integer hazardClassB;

    @Column(nullable = false)
    private String reason;
}
