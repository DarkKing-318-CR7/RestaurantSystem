package com.example.Restaurant.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}