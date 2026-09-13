package com.example.Restaurant.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Locale;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "branch_id",nullable = false)
    private Long branchId;

    // Xác định quyền hạn: ADMIN hoặc STAFF
    @Column(nullable = false)
    private String role;
}
