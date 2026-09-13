package com.example.Restaurant.repository;

import com.example.Restaurant.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    @Query("SELECT d FROM Dish d WHERE (:branchId IS NULL OR d.branchId IS NULL OR d.branchId = 0 OR d.branchId = :branchId) ORDER BY d.category ASC, d.name ASC")
    List<Dish> findAvailableDishes(@Param("branchId") Long branchId);

    List<Dish> findAllByOrderByIdDesc();
}
