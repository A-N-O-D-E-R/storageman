package com.anode.storage.entity.safety;

import java.time.LocalDateTime;

import com.anode.storage.entity.core.StorageItem;
import com.anode.storage.entity.core.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "disposal_request")
@Getter
@Setter
public class DisposalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private StorageItem product;

    private Double amount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    private String status;

    @Column(nullable = false)
    private LocalDateTime requestedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime approvedDate;

    @PrePersist
    protected void onCreate() {
        if (requestedDate == null) {
            requestedDate = LocalDateTime.now();
        }
    }
}
