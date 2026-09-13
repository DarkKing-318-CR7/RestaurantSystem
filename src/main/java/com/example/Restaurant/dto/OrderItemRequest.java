package com.example.Restaurant.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OrderItemRequest {
    private String productName;
    private Integer quantity;
    private Double price;
    private Long branchId;

    private Long sessionId;

    //nơi chứa các cấu hình động
    private Map<String,Object> customization;
}
