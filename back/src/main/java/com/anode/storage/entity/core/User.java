package com.anode.storage.entity.core;

import java.util.ArrayList;
import java.util.List;

import com.anode.storage.entity.notification.Notification;
import com.anode.storage.entity.safety.DisposalRequest;
import com.anode.storage.entity.safety.StockLog;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String passwordHash;

    private String firstName;
    private String lastName;
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<StockLog> stockLogs = new ArrayList<>();

    @OneToMany(mappedBy = "requestedBy", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("requestedBy")
    private List<DisposalRequest> requestedDisposals = new ArrayList<>();

    @OneToMany(mappedBy = "approvedBy")
    @JsonIgnoreProperties("approvedBy")
    private List<DisposalRequest> approvedDisposals = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<Notification> notifications = new ArrayList<>();
}
