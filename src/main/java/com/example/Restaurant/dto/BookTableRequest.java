package com.example.Restaurant.dto;

import lombok.Data;

@Data
public class BookTableRequest {
    private String customerPhone;
    private String customerName;
    private Integer guestCount;
    private String note;
}
