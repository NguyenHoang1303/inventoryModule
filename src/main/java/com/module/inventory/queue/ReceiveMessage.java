package com.module.inventory.queue;


import com.module.inventory.dto.OrderDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.module.inventory.queue.Config.*;

@Component
public class ReceiveMessage {

    @Autowired
    ConsumerService consumerService;


    @RabbitListener(queues = {QUEUE_INVENTORY})
    public void getInfoOrder(OrderDto orderDto) {
       consumerService.handlerInventory(orderDto);
        System.out.println("Module Inventory nhận thông tin order: " + orderDto);
    }


}
