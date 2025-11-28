package com.anode.storage.entity.safety;

import java.time.LocalDateTime;

import com.anode.storage.entity.core.StorageItem;
import com.anode.storage.entity.core.User;
import com.anode.storage.entity.location.Location;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_log")
@Getter
@Setter
@NoArgsConstructor
public class StockLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private StorageItem product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_location")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Location toLocation;

    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    // Constructor for backward compatibility
    public StockLog(Long productId, Long fromLocationId, Long toLocationId, Double amount, Long userId) {
        // This constructor is deprecated - use entity relationships instead
        this.timestamp = LocalDateTime.now();
        // Note: You should set the entity relationships directly instead
    }
}
