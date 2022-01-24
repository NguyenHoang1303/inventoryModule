package com.module.inventory.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "export_history")
public class ExportHistory {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private int quantity;
    private Long productId;
    private LocalDate createdAt;

    public ExportHistory(Long orderId, Long productId, int quantity) {
        this.orderId = orderId;
        this.quantity = quantity;
        this.productId = productId;
        this.createdAt = LocalDate.now();
    }
}
