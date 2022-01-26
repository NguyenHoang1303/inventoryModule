package com.module.inventory.entity;


import common.event.OrderDetailEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;


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

    public ExportHistory(@NotNull OrderDetailEvent orderDetail, Long orderId) {
        this.orderId = orderId;
        this.quantity = orderDetail.getQuantity();
        this.productId = orderDetail.getProductId();
        this.createdAt = LocalDate.now();
    }
}
