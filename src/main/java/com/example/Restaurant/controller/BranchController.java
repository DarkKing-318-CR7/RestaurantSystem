package com.example.Restaurant.controller;

import com.example.Restaurant.model.Branch;
import com.example.Restaurant.service.BranchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public ResponseEntity<List<Branch>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @PostMapping
    public ResponseEntity<?> createBranch(@RequestBody Branch branch) {
        try {
            return ResponseEntity.ok(branchService.createBranch(branch));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm chi nhánh: " + e.getMessage());
        }
    }
}
