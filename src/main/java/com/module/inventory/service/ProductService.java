package com.module.inventory.service;

import com.module.inventory.entity.Product;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface ProductService {
    Page getAll(int page, int pageSize);

    Product findById(Long id);

    Product save(Product product);

    void saveAll(Set<Product> products);
}
