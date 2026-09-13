package com.example.Restaurant.config;

import com.example.Restaurant.model.Branch;
import com.example.Restaurant.model.Dish;
import com.example.Restaurant.model.RestaurantTable;
import com.example.Restaurant.model.TableStatus;
import com.example.Restaurant.model.User;
import com.example.Restaurant.repository.BranchRepository;
import com.example.Restaurant.repository.DishRepository;
import com.example.Restaurant.repository.RestaurantTableRepository;
import com.example.Restaurant.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantTableRepository tableRepository;
    private final DishRepository dishRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      RestaurantTableRepository tableRepository,
                      DishRepository dishRepository,
                      BranchRepository branchRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tableRepository = tableRepository;
        this.dishRepository = dishRepository;
        this.branchRepository = branchRepository;
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

        // Tạo tài khoản Nhân viên Chi nhánh 2 (staff2)
        if (userRepository.findByUsername("staff2").isEmpty()) {
            User staff2 = new User();
            staff2.setUsername("staff2");
            staff2.setPassword(passwordEncoder.encode("123456"));
            staff2.setBranchId(2L);
            staff2.setRole("STAFF");
            userRepository.save(staff2);
        }

        // Tạo tài khoản Nhân viên Chi nhánh 3 (staff3)
        if (userRepository.findByUsername("staff3").isEmpty()) {
            User staff3 = new User();
            staff3.setUsername("staff3");
            staff3.setPassword(passwordEncoder.encode("123456"));
            staff3.setBranchId(3L);
            staff3.setRole("STAFF");
            userRepository.save(staff3);
        }

        // Seed 3 Chi nhánh THẬT vào bảng branches của PostgreSQL
        if (branchRepository.count() == 0) {
            createBranch(1L, "Chi nhánh 1 - Quận 1", "123 Nguyễn Huệ, Bến Nghé, Quận 1, TP.HCM", "028 3822 1234");
            createBranch(2L, "Chi nhánh 2 - Cầu Giấy", "45 Cầu Giấy, Quan Hoa, Cầu Giấy, Hà Nội", "024 3766 5678");
            createBranch(3L, "Chi nhánh 3 - Hải Châu", "78 Bạch Đằng, Hải Châu 1, Hải Châu, Đà Nẵng", "0236 3888 999");
        }

        // Seed bàn ăn mẫu cho từng chi nhánh nếu chi nhánh đó chưa có bàn
        if (tableRepository.findByBranchId(1L).isEmpty()) {
            createTableIfNotExists("Ban-01", 4, 1L);
            createTableIfNotExists("Ban-02", 4, 1L);
            createTableIfNotExists("Ban-03", 6, 1L);
            createTableIfNotExists("VIP-01", 10, 1L);
        }

        if (tableRepository.findByBranchId(2L).isEmpty()) {
            createTableIfNotExists("T-04", 4, 2L);
            createTableIfNotExists("T-05", 8, 2L);
            createTableIfNotExists("VIP-02", 12, 2L);
        }

        if (tableRepository.findByBranchId(3L).isEmpty()) {
            createTableIfNotExists("DN-01", 4, 3L);
            createTableIfNotExists("DN-02", 6, 3L);
            createTableIfNotExists("VIP-03", 10, 3L);
        }

        // Đảm bảo tên bàn ở Chi nhánh 1 không bị trùng lặp
        List<RestaurantTable> b1Tables = tableRepository.findByBranchId(1L);
        if (b1Tables.size() > 1 && b1Tables.stream().allMatch(t -> "Ban-VIP-01".equals(t.getTableNumber()))) {
            int idx = 1;
            for (RestaurantTable t : b1Tables) {
                t.setTableNumber(idx == 1 ? "VIP-01" : "Ban-0" + idx);
                idx++;
                tableRepository.save(t);
            }
        }

        // Seed thực đơn mẫu nếu chưa có món ăn
        if (dishRepository.count() == 0) {
            createDishIfNotExists("Pho Bo Special", "Món chính", 75000.0, "Phở bò đặc biệt nước dùng hầm 24h");
            createDishIfNotExists("Lau Bo Wagyu", "Món chính", 350000.0, "Lẩu bò Wagyu cao cấp kèm nấm kim châm");
            createDishIfNotExists("Ca Phe Sua Da", "Đồ uống", 25000.0, "Cà phê pha phin truyền thống thơm đậm đà");
            createDishIfNotExists("Bò Wagyu Nướng Tảng", "Món chính", 380000.0, "Bò Wagyu sốt tiêu đen kèm khoai tây");
            createDishIfNotExists("Lẩu Thái Hải Sản Tomyum", "Món chính", 450000.0, "Lẩu chua cay tôm, mực, nghêu tươi");
            createDishIfNotExists("Salad Cá Hồi Sốt Chanh Leo", "Khai vị", 155000.0, "Rau hữu cơ, cá hồi Nauy xông khói");
            createDishIfNotExists("Khoai Tây Chiên Phô Mai", "Khai vị", 65000.0, "Khoai tây giòn rụm rắc bột phô mai");
            createDishIfNotExists("Trà Đào Cam Sả", "Đồ uống", 45000.0, "Trà thanh mát đào miếng thơm lừng");
            createDishIfNotExists("Bia Thủ Công IPA", "Đồ uống", 68000.0, "Bia thủ công lên men hoa bia đậm vị");
            createDishIfNotExists("Panna Cotta Dâu Tây", "Tráng miệng", 55000.0, "Bánh tráng miệng mềm mịn chuẩn Ý");
        }
    }

    private void createBranch(Long id, String name, String address, String phone) {
        Branch b = new Branch();
        b.setName(name);
        b.setAddress(address);
        b.setPhone(phone);
        b.setStatus("ACTIVE");
        branchRepository.save(b);
    }

    private void createDishIfNotExists(String name, String category, Double price, String description) {
        Dish dish = new Dish();
        dish.setName(name);
        dish.setCategory(category);
        dish.setPrice(price);
        dish.setDescription(description);
        dish.setBranchId(0L); // Dùng chung cho các chi nhánh
        dish.setIsAvailable(true);
        dishRepository.save(dish);
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