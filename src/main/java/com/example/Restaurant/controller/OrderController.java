package com.example.Restaurant.controller;

import com.example.Restaurant.dto.OrderItemRequest;
import com.example.Restaurant.model.OrderItem;
import com.example.Restaurant.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/orders")
@RestController
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<OrderItem>> getOrdersBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(orderService.getItemsBySession(sessionId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDish(@RequestBody OrderItemRequest request){
        try{
            OrderItem saveItem =orderService.addDishOrder(request);
            return ResponseEntity.ok(saveItem);
        } catch(Exception e){
            return ResponseEntity.badRequest().body("lỗi thêm món: "+e.getMessage());
        }
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteDish(@PathVariable Long itemId) {
        try {
            orderService.deleteOrderItem(itemId);
            return ResponseEntity.ok("Đã xóa món thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xóa món: " + e.getMessage());
        }
    }
}
