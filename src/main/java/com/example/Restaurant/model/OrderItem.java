package com.example.Restaurant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Data
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_name",nullable = false)
    private String productName;

    @Column(name = "quantity",nullable = false)
    private Integer quantity;

    @Column(name = "price",nullable = false)
    private Double price;

    //đây là phần quan trọng ánh xạ jsonb
    //cấu trúc động
    //vào thẳng map<string,oject>của java
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "customization",columnDefinition="jsonb")
    private Map<String,Object> customization;

    @Column(name = "branch_id",nullable = false)
    private Long branchId;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}
