package com.example.Restaurant.service;

import com.example.Restaurant.dto.BillResponse;
import com.example.Restaurant.model.*;
import com.example.Restaurant.repository.DiningSessionRepository;
import com.example.Restaurant.repository.OrderItemRepository;
import com.example.Restaurant.repository.RestaurantTableRepository;
import jakarta.transaction.Transactional;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckoutService {

    private final DiningSessionRepository sessionRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantTableRepository tableRepository;

    public CheckoutService(DiningSessionRepository sessionRepository,
                           OrderItemRepository orderItemRepository,
                           RestaurantTableRepository tableRepository) {
        this.sessionRepository = sessionRepository;
        this.orderItemRepository = orderItemRepository;
        this.tableRepository = tableRepository;
    }

    @Transactional
    public BillResponse processCheckout(Long sessionId, Long branchId) {
        // 1. Kiểm tra Phiên phục vụ
        DiningSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên phục vụ!"));

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

        // 5. Trả về Hóa đơn
        return new BillResponse(session.getId(), table.getTableNumber(), totalAmount, session.getStartTime(), session.getEndTime());
    }
}