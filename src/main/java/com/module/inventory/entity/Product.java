package com.module.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name")
    private String name;

    private BigDecimal price;

    private Long supplierId;
    private int unitInStock;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;


    @Column(name = "created_at")
    private LocalDate createdAt;


    @Column(name = "updated_at")
    private LocalDate updatedAt;


    @Column(name = "deleted_at")
    private LocalDate deletedAt;

}
