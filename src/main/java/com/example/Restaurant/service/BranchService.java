package com.example.Restaurant.service;

import com.example.Restaurant.model.Branch;
import com.example.Restaurant.repository.BranchRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAllByOrderByIdAsc();
    }

    public Branch getBranchById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh với ID: " + id));
    }

    @Transactional
    public Branch createBranch(Branch branch) {
        if (branch.getName() == null || branch.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên chi nhánh không được để trống!");
        }
        branch.setStatus("ACTIVE");
        return branchRepository.save(branch);
    }
}
