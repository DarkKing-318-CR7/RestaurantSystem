package com.example.Restaurant.controller;

import com.example.Restaurant.model.Dish;
import com.example.Restaurant.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes(
            @RequestParam(required = false) Long branchId,
            @RequestHeader(value = "X-Branch-Id", required = false) Long headerBranchId) {
        Long targetBranch = branchId != null ? branchId : headerBranchId;
        return ResponseEntity.ok(dishService.getAllDishes(targetBranch));
    }

    @PostMapping
    public ResponseEntity<?> createDish(@RequestBody Dish dish) {
        try {
            Dish created = dishService.createDish(dish);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm món: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDish(@PathVariable Long id, @RequestBody Dish dish) {
        try {
            Dish updated = dishService.updateDish(id, dish);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật món: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDish(@PathVariable Long id) {
        try {
            dishService.deleteDish(id);
            return ResponseEntity.ok("Đã xóa món ăn khỏi thực đơn thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xóa món: " + e.getMessage());
        }
    }
}
