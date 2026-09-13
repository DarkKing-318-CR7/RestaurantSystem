package com.example.Restaurant.controller;

import com.example.Restaurant.dto.OrderItemRequest;
import com.example.Restaurant.model.OrderItem;
import com.example.Restaurant.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/orders")
@RestController
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDish(@RequestBody OrderItemRequest request){
        try{
            OrderItem saveItem =orderService.addDishOrder(request);
            return ResponseEntity.ok(saveItem);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("lỗi thêm món: "+e.getMessage());
        }
    }
}
