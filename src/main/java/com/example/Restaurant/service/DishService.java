package com.example.Restaurant.service;

import com.example.Restaurant.config.TenantContext;
import com.example.Restaurant.model.Dish;
import com.example.Restaurant.repository.DishRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> getAllDishes(Long targetBranchId) {
        Long branch = targetBranchId;
        String role = TenantContext.getCurrentUserRole();
        Long currentBranch = TenantContext.getCurrentBranch();

        if (!"ADMIN".equals(role) && currentBranch != null && currentBranch > 0) {
            branch = currentBranch;
        }

        return dishRepository.findAvailableDishes(branch);
    }

    @Transactional
    public Dish createDish(Dish dish) {
        String role = TenantContext.getCurrentUserRole();
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Chỉ Quản trị viên (ADMIN) mới có quyền thêm món ăn vào thực đơn!");
        }

        if (dish.getName() == null || dish.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên món không được để trống!");
        }
        if (dish.getPrice() == null || dish.getPrice() < 0) {
            throw new RuntimeException("Giá món ăn không hợp lệ!");
        }
        if (dish.getCategory() == null || dish.getCategory().trim().isEmpty()) {
            dish.setCategory("Món chính");
        }
        if (dish.getIsAvailable() == null) {
            dish.setIsAvailable(true);
        }

        return dishRepository.save(dish);
    }

    @Transactional
    public Dish updateDish(Long id, Dish dishReq) {
        String role = TenantContext.getCurrentUserRole();
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Chỉ Quản trị viên (ADMIN) mới có quyền sửa món ăn!");
        }

        Dish existing = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn với ID: " + id));

        if (dishReq.getName() != null && !dishReq.getName().trim().isEmpty()) {
            existing.setName(dishReq.getName());
        }
        if (dishReq.getPrice() != null && dishReq.getPrice() >= 0) {
            existing.setPrice(dishReq.getPrice());
        }
        if (dishReq.getCategory() != null && !dishReq.getCategory().trim().isEmpty()) {
            existing.setCategory(dishReq.getCategory());
        }
        if (dishReq.getDescription() != null) {
            existing.setDescription(dishReq.getDescription());
        }
        if (dishReq.getBranchId() != null) {
            existing.setBranchId(dishReq.getBranchId());
        }
        if (dishReq.getIsAvailable() != null) {
            existing.setIsAvailable(dishReq.getIsAvailable());
        }

        return dishRepository.save(existing);
    }

    @Transactional
    public void deleteDish(Long id) {
        String role = TenantContext.getCurrentUserRole();
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Chỉ Quản trị viên (ADMIN) mới có quyền xóa món ăn khỏi thực đơn!");
        }

        Dish existing = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn cần xóa với ID: " + id));

        dishRepository.delete(existing);
    }
}
