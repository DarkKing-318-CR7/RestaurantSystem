package com.example.Restaurant.controller;

import com.example.Restaurant.model.DiningSession;
import com.example.Restaurant.model.RestaurantTable;
import com.example.Restaurant.model.SessionStatus;
import com.example.Restaurant.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping("/{tableId}/book")
    public ResponseEntity<?> bookTable(@PathVariable("tableId") Long tableId) {
        try {
            DiningSession session = tableService.bookTable(tableId);
            return ResponseEntity.ok(session);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Lỗi: "+e.getMessage());
        }
    }
}
