package com.example.Restaurant.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dinning_sessions")
public class DiningSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_id",nullable = false)
    private Long tableId;

    @Column(name = "branch_id",nullable = false)
    private Long branchId;

    @Enumerated(EnumType.STRING)
    @Column(name ="status",nullable = false)
    private SessionStatus status=SessionStatus.OPEN;

    @Column(name = "start_time",nullable = false)
    private LocalDateTime startTime=LocalDateTime.now();
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "guest_count")
    private Integer guestCount = 1;

    @Column(name = "note")
    private String note;
}
