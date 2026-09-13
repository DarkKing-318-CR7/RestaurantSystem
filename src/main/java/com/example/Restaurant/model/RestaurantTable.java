package com.example.Restaurant.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Data
@Table(name = "restaurant_tables")
//1. khai báo bộ lọc nhận vào một tham số kiểm Long
@FilterDef(name="branchFilter",parameters =@ParamDef(name="branchIdParam",type = Long.class))
//2.ĐỊnh nghĩa câu lẹnh SQL sẽ được kết nối
@Filter(name = "branchFilter",condition = "branch_id= :branchIdParam")
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number",nullable = false)
    private String  tableNumber;

    @Column(name = "capacity",nullable = false)
    private Integer  capacity;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private TableStatus status=TableStatus.AVAILABLE;

    @Column(name = "branch_id",nullable = false)
    private Long branchId;

    //phần cốt lỗi annotation báo cho jpa biết dùng cột này để khóa lạc quan
    @Version
    private Long version;
}
