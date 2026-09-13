package com.example.Restaurant.service;

import com.example.Restaurant.config.TenantContext;
import com.example.Restaurant.dto.BillResponse;
import com.example.Restaurant.model.*;
import com.example.Restaurant.repository.CustomerRepository;
import com.example.Restaurant.repository.DiningSessionRepository;
import com.example.Restaurant.repository.OrderItemRepository;
import com.example.Restaurant.repository.RestaurantTableRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutService {

    private final DiningSessionRepository sessionRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantTableRepository tableRepository;
    private final CustomerRepository customerRepository;

    public CheckoutService(DiningSessionRepository sessionRepository,
                           OrderItemRepository orderItemRepository,
                           RestaurantTableRepository tableRepository,
                           CustomerRepository customerRepository) {
        this.sessionRepository = sessionRepository;
        this.orderItemRepository = orderItemRepository;
        this.tableRepository = tableRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public BillResponse processCheckout(Long sessionId, Long branchId) {
        // 1. Kiểm tra Phiên phục vụ
        DiningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên phục vụ!"));

        // Bảo mật Multi-tenant: Nhân viên chỉ được thanh toán cho phiên phục vụ của chi nhánh mình
        Long currentBranch = TenantContext.getCurrentBranch();
        String currentRole = TenantContext.getCurrentUserRole();
        if (!"ADMIN".equals(currentRole) && currentBranch != null && !session.getBranchId().equals(currentBranch)) {
            throw new RuntimeException("Bạn không có quyền thanh toán cho phiên phục vụ thuộc chi nhánh khác!");
        }

        if (session.getStatus() == SessionStatus.CLOSED) {
            throw new RuntimeException("Phiên phục vụ này đã được thanh toán!");
        }

        // 2. Tính tổng tiền các món đã gọi
        List<OrderItem> orderedItems = orderItemRepository.findBySessionId(sessionId);
        double totalAmount = 0.0;
        for (OrderItem item : orderedItems) {
            totalAmount += (item.getPrice() * item.getQuantity());
        }

        // 3. Đóng Phiên phục vụ (Cập nhật trạng thái và thời gian kết thúc)
        session.setStatus(SessionStatus.CLOSED);
        session.setEndTime(LocalDateTime.now());
        sessionRepository.save(session);

        // 4. Giải phóng Bàn (Đưa trạng thái về AVAILABLE)
        RestaurantTable table = tableRepository.findById(session.getTableId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn hợp lệ!"));
        table.setStatus(TableStatus.AVAILABLE);

        // Cơ chế Optimistic Locking vẫn tự động chạy ở bước save này để bảo vệ dữ liệu bàn
        tableRepository.save(table);

        // 5. Xử lý tích điểm thành viên (nếu có thông tin khách hàng)
        String customerName = "Khách vãng lai";
        String customerPhone = null;
        int pointsEarned = 0;
        int totalPoints = 0;

        if (session.getCustomerId() != null) {
            Customer customer = customerRepository.findById(session.getCustomerId()).orElse(null);
            if (customer != null) {
                customerName = customer.getName();
                customerPhone = customer.getPhone();

                // Quy tắc: 10,000 VNĐ = 1 điểm thưởng
                pointsEarned = (int) (totalAmount / 10000.0);
                int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
                totalPoints = currentPoints + pointsEarned;
                customer.setLoyaltyPoints(totalPoints);
                customerRepository.save(customer);
            }
        }

        // 6. Trả về Hóa đơn hoàn chỉnh
        BillResponse bill = new BillResponse();
        bill.setSessionId(session.getId());
        bill.setTableNumber(table.getTableNumber());
        bill.setTotalAmount(totalAmount);
        bill.setStartTime(session.getStartTime());
        bill.setEndTime(session.getEndTime());
        bill.setCustomerName(customerName);
        bill.setCustomerPhone(customerPhone);
        bill.setLoyaltyPointsEarned(pointsEarned);
        bill.setTotalLoyaltyPoints(totalPoints);
        bill.setGuestCount(session.getGuestCount() != null ? session.getGuestCount() : 1);

        return bill;
    }
}