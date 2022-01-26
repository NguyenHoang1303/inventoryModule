package com.module.inventory.entity;

import common.event.OrderDetailEvent;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(name = "import_history")
public class ImportHistory {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private int quantity;
    private Long orderId;
    private BigDecimal unitPrice;
    private Long productId;
    private LocalDate createdAt;

    public ImportHistory(@NotNull OrderDetailEvent product, Long orderId) {
        this.orderId = orderId;
        this.productId = product.getProductId();
        this.unitPrice = product.getUnitPrice();
        this.quantity = product.getQuantity();
        this.createdAt = LocalDate.now();
        this.createdAt = LocalDate.now();
    }
}
