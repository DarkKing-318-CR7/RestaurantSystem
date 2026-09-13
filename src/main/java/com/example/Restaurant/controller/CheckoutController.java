package com.example.Restaurant.controller;

import com.example.Restaurant.dto.BillResponse;
import com.example.Restaurant.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/history")
    public ResponseEntity<java.util.List<BillResponse>> getHistory(
            @RequestParam(required = false) Long branchId,
            @RequestHeader(value = "X-Branch-Id", required = false) Long headerBranchId) {
        Long targetBranch = branchId != null ? branchId : headerBranchId;
        return ResponseEntity.ok(checkoutService.getCheckoutHistory(targetBranch));
    }

    // API: POST http://localhost:8080/api/checkout/{sessionId}?branchId=1
    @PostMapping("/{sessionId}")
    public ResponseEntity<?> checkout(@PathVariable Long sessionId, @RequestParam Long branchId) {
        try {
            BillResponse bill = checkoutService.processCheckout(sessionId, branchId);
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thanh toán: " + e.getMessage());
        }
    }
}
