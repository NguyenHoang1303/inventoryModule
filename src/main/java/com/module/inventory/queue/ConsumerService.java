package com.module.inventory.queue;

import com.module.inventory.translate.TranslationService;
import common.event.OrderDetailEvent;
import common.event.OrderEvent;
import com.module.inventory.entity.ExportHistory;
import com.module.inventory.entity.ImportHistory;
import com.module.inventory.entity.Product;
import com.module.inventory.enums.InventoryStatus;
import com.module.inventory.repository.ExportRepository;
import com.module.inventory.repository.ImportRepository;
import com.module.inventory.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.module.inventory.constant.KeyI18n.*;
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

    @Autowired
    TranslationService translationService;


    @Transactional
    public void handlerInventory(@NotNull OrderEvent orderEvent) {
        orderEvent.setQueueName(QUEUE_INVENTORY);
        if (!orderEvent.validationInventory()) {
            orderEvent.setMessage(translationService.translate(CHECK_INFO_INVENTORY));
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, orderEvent);
            return;
        }
        if (orderEvent.getInventoryStatus().equals(InventoryStatus.PENDING.name())) {
            handlerPendingStatus(orderEvent);
            return;
        }
        if (orderEvent.getInventoryStatus().equals(InventoryStatus.RETURN.name())) {
            handlerReturnStatus(orderEvent);
        }
    }

    @Transactional
    public void handlerReturnStatus(@NotNull OrderEvent orderEvent) {
        Set<Product> products = new HashSet<>();
        Set<ImportHistory> importHistories = new HashSet<>();
        for (OrderDetailEvent odt : orderEvent.getOrderDetailEvents()) {
            Product product = handlerProductNotExist(odt.getProductId(), orderEvent);
            if (product == null) return;
            int quantity = odt.getQuantity();
            int unitInStock = product.getUnitInStock();
            product.setUnitInStock(unitInStock + quantity);
            importHistories.add(new ImportHistory(odt, orderEvent.getOrderId()));
            products.add(product);
        }

        try {
            productService.saveAll(products);
            importRepository.saveAll(importHistories);
            orderEvent.setInventoryStatus(InventoryStatus.RETURNED.name());
            orderEvent.setMessage(translationService.translate(SUCCESS_RETURN));
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, orderEvent);
        } catch (Exception e) {
            orderEvent.setInventoryStatus(InventoryStatus.PENDING.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, orderEvent);
            throw new RuntimeException(e.getMessage());
        }


    }

    private void handlerPendingStatus(@NotNull OrderEvent orderEvent) {
        Set<Product> products = new HashSet<>();
        Set<ExportHistory> exportHistorySet = new HashSet<>();
        for (OrderDetailEvent odt : orderEvent.getOrderDetailEvents()) {
            Product product = handlerProductNotExist(odt.getProductId(), orderEvent);
            if (product == null) return;
            int quantity = odt.getQuantity();
            int unitInStock = product.getUnitInStock();
            if (quantity > unitInStock) {
                orderEvent.setMessage(translationService.translate(NOT_ENOUGH_QUANTITY));
                orderEvent.setInventoryStatus(InventoryStatus.OUT_OF_STOCK.name());
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, orderEvent);
                return;
            }
            product.setUnitInStock(unitInStock - quantity);
            exportHistorySet.add(new ExportHistory(odt, orderEvent.getOrderId()));
            products.add(product);
        }

        try {
            exportRepository.saveAll(exportHistorySet);
            productService.saveAll(products);
            orderEvent.setMessage(translationService.translate(SUCCESS_DONE));
            orderEvent.setInventoryStatus(InventoryStatus.DONE.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, orderEvent);
        } catch (Exception e) {
            orderEvent.setInventoryStatus(InventoryStatus.RETURN.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, orderEvent);
            throw new RuntimeException(e.getMessage());
        }

    }

    private @Nullable Product handlerProductNotExist(Long id, OrderEvent orderEvent) {
        Product product = productService.findById(id);
        if (product != null) {
            return product;
        }
        orderEvent.setMessage(translationService.translate(PRODUCT_NOTFOUND));
        rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, orderEvent);
        return null;
    }

}
