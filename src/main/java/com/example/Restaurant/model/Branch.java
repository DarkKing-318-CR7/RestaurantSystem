package com.example.Restaurant.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status = "ACTIVE"; // ACTIVE, INACTIVE
}
