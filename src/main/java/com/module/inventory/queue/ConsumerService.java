package com.module.inventory.queue;

import com.module.inventory.dto.OrderDetailDto;
import com.module.inventory.dto.OrderDto;
import com.module.inventory.entity.ExportHistory;
import com.module.inventory.entity.ImportHistory;
import com.module.inventory.entity.Product;
import com.module.inventory.enums.InventoryStatus;
import com.module.inventory.repository.ExportRepository;
import com.module.inventory.repository.ImportRepository;
import com.module.inventory.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.module.inventory.queue.Config.*;

@Service
@Log4j2
public class ConsumerService {

    @Autowired
    ExportRepository exportRepository;

    @Autowired
    ImportRepository importRepository;

    @Autowired
    ProductService productService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Transactional
    public void handlerInventory(OrderDto orderDto) {
        if (!orderDto.validationInventory()) {
            orderDto.setMessage("Kiểm tra thông tin order");
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER_INVENTORY, orderDto);
            return;
        }

        if (orderDto.getInventoryStatus().equals(InventoryStatus.PENDING.name())) {
            handlerPendingStatus(orderDto);
            return;
        }

        if (orderDto.getInventoryStatus().equals(InventoryStatus.RETURN.name())) {
            handlerReturnStatus(orderDto);
        }
    }

    @Transactional
    public void handlerReturnStatus(OrderDto orderDto) {
        Set<Product> products = new HashSet<>();
        Set<ImportHistory> importHistories = new HashSet<>();
        for (OrderDetailDto odt: orderDto.getOrderDetails()) {
            Product product = productService.findById(odt.getProductId());
            if (product == null) {
                orderDto.setMessage("Sản phẩm không tồn tại.");
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER_INVENTORY, orderDto);
                return;
            }

            int quantity = odt.getQuantity();
            int unitInStock = product.getUnitInStock();
            product.setUnitInStock(unitInStock + quantity);
            importHistories.add(new ImportHistory(product.getId(), product.getSupplierId(),
                    product.getPrice(), quantity));
            products.add(product);
        }

        try {
            productService.saveAll(products);
            importRepository.saveAll(importHistories);
            orderDto.setInventoryStatus(InventoryStatus.RETURNED.name());
            orderDto.setMessage("Xử lý đơn hàng thành công.");
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER_INVENTORY, orderDto);
        } catch (Exception e) {
            orderDto.setInventoryStatus(InventoryStatus.PENDING.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, orderDto);
            throw new RuntimeException(e.getMessage());
        }


    }

    private void handlerPendingStatus(OrderDto orderDto) {
        Set<Product> products = new HashSet<>();
        Set<ExportHistory> exportHistorySet = new HashSet<>();
        for (OrderDetailDto odt: orderDto.getOrderDetails()) {
            Product product = productService.findById(odt.getProductId());
            if (product == null) {
                orderDto.setMessage("Sản phẩm không tồn tại.");
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER_INVENTORY, orderDto);
                return;
            }

            int quantity = odt.getQuantity();
            int unitInStock = product.getUnitInStock();
            if (quantity > unitInStock) {
                orderDto.setMessage("Số lượng phẩm trong kho không đủ. ");
                orderDto.setInventoryStatus(InventoryStatus.OUT_OF_STOCK.name());
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER_INVENTORY, orderDto);
                return;
            }
            product.setUnitInStock(unitInStock - quantity);
            exportHistorySet.add(new ExportHistory(orderDto.getOrderId(), product.getId(), quantity));
            products.add(product);
        }
        try {
            exportRepository.saveAll(exportHistorySet);
            productService.saveAll(products);
            orderDto.setMessage("Xử lý đơn hàng thành công.");
            orderDto.setInventoryStatus(InventoryStatus.DONE.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER_INVENTORY, orderDto);
        } catch (Exception e) {
            orderDto.setInventoryStatus(InventoryStatus.RETURN.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, orderDto);
            throw new RuntimeException(e.getMessage());
        }

    }

}
