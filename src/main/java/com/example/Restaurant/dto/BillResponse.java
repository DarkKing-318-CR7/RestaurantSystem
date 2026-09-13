package com.example.Restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private Long sessionId;
    private String tableNumber;
    private Double totalAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String customerName;
    private String customerPhone;
    private Integer loyaltyPointsEarned;
    private Integer totalLoyaltyPoints;
    private Integer guestCount;

    public BillResponse(Long sessionId, String tableNumber, Double totalAmount, LocalDateTime startTime, LocalDateTime endTime) {
        this.sessionId = sessionId;
        this.tableNumber = tableNumber;
        this.totalAmount = totalAmount;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}