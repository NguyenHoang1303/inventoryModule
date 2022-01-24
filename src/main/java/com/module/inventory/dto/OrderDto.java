package com.module.inventory.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    private Long orderId;
    private Long userId;
    private Set<OrderDetailDto> orderDetails;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private String inventoryStatus;
    private String orderStatus;
    private String device_token;
    private String message;

    public boolean validationInventory(){
        return this.orderDetails.size() > 0 && this.orderId != null && this.orderStatus != null && this.inventoryStatus != null;
    }
}