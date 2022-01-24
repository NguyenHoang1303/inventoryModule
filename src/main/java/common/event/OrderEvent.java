package common.event;

import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderEvent {

    private Long orderId;
    private Long userId;
    private Set<OrderDetailEvent> orderDetailEvents = new HashSet<>();
    private BigDecimal totalPrice;
    private String paymentStatus;
    private String inventoryStatus;
    private String orderStatus;
    private String device_token;
    private String message;
    private String queueName;

    public boolean validationInventory(){
        return this.orderDetailEvents.size() > 0 && this.orderId != null && this.orderStatus != null && this.inventoryStatus != null;
    }


}