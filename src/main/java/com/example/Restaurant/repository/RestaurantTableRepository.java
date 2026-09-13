package com.example.Restaurant.repository;

import com.example.Restaurant.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    // Custom query: Tìm bàn theo ID nhưng phải đảm bảo thuộc đúng Chi nhánh
    Optional<RestaurantTable> findById(Long tableId);

    List<RestaurantTable> findByBranchId(Long branchId);

    boolean existsByBranchIdAndTableNumber(Long branchId, String tableNumber);

    boolean existsByBranchIdAndTableNumberAndIdNot(Long branchId, String tableNumber, Long id);
}