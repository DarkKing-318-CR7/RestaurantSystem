package com.example.Restaurant.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "dishes")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category; // Món chính, Khai vị, Đồ uống, Tráng miệng...

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "branch_id")
    private Long branchId; // 0L hoặc null: áp dụng cho tất cả chi nhánh, hoặc > 0 cho chi nhánh cụ thể

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
}
