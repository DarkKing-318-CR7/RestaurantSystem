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
        String currentRole = TenantContext.getCurrentUserRole();
        Long currentBranch = TenantContext.getCurrentBranch();

        // 1. Bảo mật Multi-tenant: Nhân viên (STAFF) BẮT BUỘC chỉ được xem chi nhánh của mình
        if (!"ADMIN".equals(currentRole)) {
            Long userBranch = (currentBranch != null && currentBranch > 0) ? currentBranch : 1L;
            return restaurantTableRepository.findByBranchId(userBranch);
        }

        // 2. Nếu là Tổng quản lý (ADMIN): Cho phép lọc theo chi nhánh yêu cầu hoặc xem tất cả
        if (targetBranchId != null && targetBranchId > 0) {
            return restaurantTableRepository.findByBranchId(targetBranchId);
        }
        return restaurantTableRepository.findAll();
    }

    public Optional<DiningSession> getActiveSession(Long tableId) {
        RestaurantTable table = restaurantTableRepository.findById(tableId).orElse(null);
        if (table == null) {
            return Optional.empty();
        }

        String currentRole = TenantContext.getCurrentUserRole();
        Long currentBranch = TenantContext.getCurrentBranch();
        if (!"ADMIN".equals(currentRole) && currentBranch != null && currentBranch > 0 && !table.getBranchId().equals(currentBranch)) {
            throw new RuntimeException("Bạn không có quyền xem phiên phục vụ của bàn thuộc chi nhánh khác!");
        }

        return sessionRepository.findFirstByTableIdAndStatusOrderByIdDesc(tableId, SessionStatus.OPEN);
    }

    @Transactional
    public RestaurantTable createTable(RestaurantTable table) {
        String currentRole = TenantContext.getCurrentUserRole();
        if (!"ADMIN".equals(currentRole)) {
            throw new RuntimeException("Chỉ Quản trị viên (ADMIN) mới có quyền thêm bàn mới!");
        }

        if (table.getTableNumber() == null || table.getTableNumber().trim().isEmpty()) {
            throw new RuntimeException("Tên hoặc số bàn không được để trống!");
        }
        if (table.getCapacity() == null || table.getCapacity() <= 0) {
            throw new RuntimeException("Số chỗ ngồi của bàn phải lớn hơn 0!");
        }
        if (table.getBranchId() == null || table.getBranchId() <= 0) {
            table.setBranchId(1L);
        }

        // Kiểm tra xem mã bàn đã tồn tại trong chi nhánh chưa
        if (restaurantTableRepository.existsByBranchIdAndTableNumber(table.getBranchId(), table.getTableNumber().trim())) {
            throw new RuntimeException("Mã bàn '" + table.getTableNumber() + "' đã tồn tại ở Chi nhánh " + table.getBranchId() + "!");
        }

        table.setTableNumber(table.getTableNumber().trim());
        table.setStatus(TableStatus.AVAILABLE);
        return restaurantTableRepository.save(table);
    }

    @Transactional
    public RestaurantTable updateTable(Long tableId, RestaurantTable req) {
        String currentRole = TenantContext.getCurrentUserRole();
        if (!"ADMIN".equals(currentRole)) {
            throw new RuntimeException("Chỉ Quản trị viên (ADMIN) mới có quyền sửa thông tin bàn!");
        }

        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn với ID: " + tableId));

        if (req.getTableNumber() != null && !req.getTableNumber().trim().isEmpty()) {
            String newNumber = req.getTableNumber().trim();
            Long targetBranch = (req.getBranchId() != null && req.getBranchId() > 0) ? req.getBranchId() : table.getBranchId();
            if (restaurantTableRepository.existsByBranchIdAndTableNumberAndIdNot(targetBranch, newNumber, tableId)) {
                throw new RuntimeException("Mã bàn '" + newNumber + "' đã được sử dụng ở chi nhánh này!");
            }
            table.setTableNumber(newNumber);
        }

        if (req.getCapacity() != null && req.getCapacity() > 0) {
            table.setCapacity(req.getCapacity());
        }

        if (req.getBranchId() != null && req.getBranchId() > 0) {
            table.setBranchId(req.getBranchId());
        }

        return restaurantTableRepository.save(table);
    }

    @Transactional
    public void deleteTable(Long tableId) {
        String currentRole = TenantContext.getCurrentUserRole();
        if (!"ADMIN".equals(currentRole)) {
            throw new RuntimeException("Chỉ Quản trị viên (ADMIN) mới có quyền xóa bàn!");
        }

        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn với ID: " + tableId));

        // Kiểm tra xem bàn có đang mở phiên phục vụ không
        Optional<DiningSession> activeSession = sessionRepository.findFirstByTableIdAndStatusOrderByIdDesc(tableId, SessionStatus.OPEN);
        if (activeSession.isPresent() || table.getStatus() != TableStatus.AVAILABLE) {
            throw new RuntimeException("Bàn đang có khách hoặc đang trong phiên phục vụ, không thể xóa!");
        }

        restaurantTableRepository.delete(table);
    }
}
