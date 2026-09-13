package com.example.Restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BillResponse {
    private Long sessionId;
    private String tableNumber;
    private Double totalAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}