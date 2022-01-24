package com.module.inventory.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "Số lượng sản phẩm không được để trống")
    @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0")
    private int quantity;

    @NotNull(message = "Đơn giá không được để trống")
    @Min(value = 1, message = "Giá sản phẩm phải lớn hơn 0")
    private BigDecimal unitPrice;

    @NotNull(message = "Sản phẩm không được để trống")
    private Long productId;

    @NotNull(message = "Nhà cung cấp không được để trống")
    private Long supplierId;

    private LocalDate createdAt;

    public ImportHistory(Long productId, Long supplierId, BigDecimal unitPrice, int quantity) {
        this.productId = productId;
        if (supplierId == null){
            supplierId = 1L;
        }
        this.supplierId = supplierId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.createdAt = LocalDate.now();
    }
}
