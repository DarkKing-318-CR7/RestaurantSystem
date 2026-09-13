package com.example.Restaurant.controller;

import com.example.Restaurant.dto.BookTableRequest;
import com.example.Restaurant.model.DiningSession;
import com.example.Restaurant.model.RestaurantTable;
import com.example.Restaurant.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantTable>> getAllTables(
            @RequestParam(required = false) Long branchId,
            @RequestHeader(value = "X-Branch-Id", required = false) Long headerBranchId) {
        Long targetBranch = branchId != null ? branchId : headerBranchId;
        return ResponseEntity.ok(tableService.getAllTables(targetBranch));
    }

    @GetMapping("/{tableId}/active-session")
    public ResponseEntity<?> getActiveSession(@PathVariable("tableId") Long tableId) {
        return tableService.getActiveSession(tableId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{tableId}/book")
    public ResponseEntity<?> bookTable(
            @PathVariable("tableId") Long tableId,
            @RequestBody(required = false) BookTableRequest request) {
        try {
            DiningSession session = tableService.bookTable(tableId, request);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody RestaurantTable table) {
        try {
            RestaurantTable created = tableService.createTable(table);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm bàn: " + e.getMessage());
        }
    }

    @PutMapping("/{tableId}")
    public ResponseEntity<?> updateTable(
            @PathVariable("tableId") Long tableId,
            @RequestBody RestaurantTable request) {
        try {
            RestaurantTable updated = tableService.updateTable(tableId, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật bàn: " + e.getMessage());
        }
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<?> deleteTable(@PathVariable("tableId") Long tableId) {
        try {
            tableService.deleteTable(tableId);
            return ResponseEntity.ok("Đã xóa bàn thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xóa bàn: " + e.getMessage());
        }
    }
}
