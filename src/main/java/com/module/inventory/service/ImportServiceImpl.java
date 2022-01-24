package com.module.inventory.service;

import com.module.inventory.entity.ImportHistory;
import com.module.inventory.entity.Product;
import com.module.inventory.exception.NotFoundException;
import com.module.inventory.repository.ImportRepository;
import com.module.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    ImportRepository importRepository;

    @Autowired
    ProductRepository productRepository;

    @Transactional
    @Override
    public ImportHistory save(ImportHistory importHistory) {
        Product product = productRepository.findById(importHistory.getProductId()).orElse(null);
        if (product == null) {
            throw new NotFoundException("Sản phẩm không tồn tại");
        }

        try {
            product.setCreatedAt(LocalDate.now());
            product.setUnitInStock(importHistory.getQuantity());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return importRepository.save(importHistory);
    }

}
