package com.anode.storage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_logs")
@Getter
@Setter
@NoArgsConstructor
public class StockLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long fromLocationId;
    private Long toLocationId;
    private Double amount;
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public StockLog(Long productId, Long fromLocationId, Long toLocationId, Double amount, Long userId) {
        this.productId = productId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.amount = amount;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }
}
