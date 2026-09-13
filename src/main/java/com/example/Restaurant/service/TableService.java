package com.example.Restaurant.service;

import com.example.Restaurant.config.TenantContext;
import com.example.Restaurant.dto.BookTableRequest;
import com.example.Restaurant.model.Customer;
import com.example.Restaurant.model.DiningSession;
import com.example.Restaurant.model.RestaurantTable;
import com.example.Restaurant.model.SessionStatus;
import com.example.Restaurant.model.TableStatus;
import com.example.Restaurant.repository.DiningSessionRepository;
import com.example.Restaurant.repository.RestaurantTableRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TableService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final DiningSessionRepository sessionRepository;
    private final CustomerService customerService;

    public TableService(RestaurantTableRepository restaurantTableRepository,
                        DiningSessionRepository sessionRepository,
                        CustomerService customerService) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.sessionRepository = sessionRepository;
        this.customerService = customerService;
    }

    @Transactional
    public DiningSession bookTable(Long tableId) {
        return bookTable(tableId, null);
    }

    @Transactional
    public DiningSession bookTable(Long tableId, BookTableRequest request) {
        // 1. Tìm bàn trong database
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn hợp lệ"));

        // 2. Bảo mật Multi-tenant: Kiểm tra chi nhánh bàn có khớp với nhân viên không (trừ ADMIN)
        Long currentBranch = TenantContext.getCurrentBranch();
        String currentRole = TenantContext.getCurrentUserRole();
        if (!"ADMIN".equals(currentRole) && currentBranch != null && !table.getBranchId().equals(currentBranch)) {
            throw new RuntimeException("Bạn không có quyền thao tác trên bàn thuộc chi nhánh khác!");
        }

        // 3. Kiểm tra trạng thái bàn
        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new RuntimeException("Bàn này không còn trống hoặc đã có người đặt!");
        }

        // 4. Đổi trạng thái bàn sang đã đặt (RESERVED)
        table.setStatus(TableStatus.RESERVED);
        restaurantTableRepository.save(table);

        // 5. Mở phiên phục vụ mới
        DiningSession session = new DiningSession();
        session.setTableId(tableId);
        session.setBranchId(table.getBranchId());
        session.setStatus(SessionStatus.OPEN);

        // 6. Xử lý thông tin khách hàng (nếu có)
        if (request != null) {
            if (request.getCustomerPhone() != null && !request.getCustomerPhone().trim().isEmpty()) {
                Customer customer = customerService.getOrCreateCustomer(
                        request.getCustomerPhone(),
                        request.getCustomerName(),
                        table.getBranchId()
                );
                if (customer != null) {
                    session.setCustomerId(customer.getId());
                }
            }

            if (request.getGuestCount() != null && request.getGuestCount() > 0) {
                session.setGuestCount(request.getGuestCount());
            }

            session.setNote(request.getNote());
        }

        return sessionRepository.save(session);
    }

    public List<RestaurantTable> getAllTables(Long targetBranchId) {
        if (targetBranchId != null && targetBranchId > 0) {
            return restaurantTableRepository.findByBranchId(targetBranchId);
        }
        Long currentBranch = TenantContext.getCurrentBranch();
        String currentRole = TenantContext.getCurrentUserRole();
        if (!"ADMIN".equals(currentRole) && currentBranch != null && currentBranch > 0) {
            return restaurantTableRepository.findByBranchId(currentBranch);
        }
        return restaurantTableRepository.findAll();
    }

    public Optional<DiningSession> getActiveSession(Long tableId) {
        return sessionRepository.findFirstByTableIdAndStatusOrderByIdDesc(tableId, SessionStatus.OPEN);
    }
}
