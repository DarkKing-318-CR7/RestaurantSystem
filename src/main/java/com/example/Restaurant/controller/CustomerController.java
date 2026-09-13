package com.example.Restaurant.controller;

import com.example.Restaurant.config.TenantContext;
import com.example.Restaurant.dto.CustomerRequest;
import com.example.Restaurant.model.Customer;
import com.example.Restaurant.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/by-phone")
    public ResponseEntity<?> getCustomerByPhone(@RequestParam String phone) {
        return customerService.findByPhone(phone)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRequest request) {
        try {
            Long branchId = TenantContext.getCurrentBranch();
            Customer customer = customerService.createCustomer(request, branchId);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm khách hàng: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(customerService.search(keyword));
    }
}
