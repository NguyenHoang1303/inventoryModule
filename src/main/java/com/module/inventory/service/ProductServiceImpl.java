package com.module.inventory.service;

import com.module.inventory.entity.Product;
import com.module.inventory.repository.ProductRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepo;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public Page getAll(int page, int pageSize){
        if (page <= 0 ){
            page = 1;
        }
        if (pageSize < 0){
            page = 9;
        }
        return productRepo.findAll(PageRequest.of(page - 1, pageSize));
    }
    @Override
    public Product findById(Long id){
        return productRepo.findById(id).orElse(null);
    }

    @Override
    public Product save(Product product){
        return productRepo.save(product);
    }

    @Override
    public void saveAll(Set<Product> products) {
        productRepo.saveAll(products);
    }


}
