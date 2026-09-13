package com.example.Restaurant.config;

import com.example.Restaurant.model.RestaurantTable;
import com.example.Restaurant.model.TableStatus;
import com.example.Restaurant.model.User;
import com.example.Restaurant.repository.RestaurantTableRepository;
import com.example.Restaurant.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantTableRepository tableRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      RestaurantTableRepository tableRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tableRepository = tableRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Tạo tài khoản Nhân viên (STAFF) thuộc Chi nhánh 1
        if (userRepository.findByUsername("staff1").isEmpty()) {
            User staff = new User();
            staff.setUsername("staff1");
            staff.setPassword(passwordEncoder.encode("123456"));
            staff.setBranchId(1L);
            staff.setRole("STAFF");
            userRepository.save(staff);
        }

        // Tạo tài khoản Tổng quản lý (ADMIN)
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setBranchId(0L); // Admin không phụ thuộc chi nhánh cụ thể
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        // Seed bàn ăn mẫu nếu chưa có đủ bàn
        if (tableRepository.count() < 4) {
            createTableIfNotExists("T-02", 2, 1L);
            createTableIfNotExists("T-03", 6, 1L);
            createTableIfNotExists("VIP-01", 10, 1L);
            createTableIfNotExists("T-04", 4, 2L);
            createTableIfNotExists("T-05", 8, 2L);
        }
    }

    private void createTableIfNotExists(String tableNumber, int capacity, Long branchId) {
        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(tableNumber);
        table.setCapacity(capacity);
        table.setBranchId(branchId);
        table.setStatus(TableStatus.AVAILABLE);
        tableRepository.save(table);
    }
}