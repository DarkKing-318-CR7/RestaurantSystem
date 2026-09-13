package com.example.Restaurant.service;

import com.example.Restaurant.config.TenantContext;
import com.example.Restaurant.dto.OrderItemRequest;
import com.example.Restaurant.model.DiningSession;
import com.example.Restaurant.model.OrderItem;
import com.example.Restaurant.repository.DiningSessionRepository;
import com.example.Restaurant.repository.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderItemRepository orderItemRepository;
    private final DiningSessionRepository sessionRepository;

    public OrderService(OrderItemRepository orderItemRepository, DiningSessionRepository sessionRepository) {
        this.orderItemRepository = orderItemRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public OrderItem addDishOrder(OrderItemRequest request) {
        String currentRole = TenantContext.getCurrentUserRole();
        Long currentBranch = TenantContext.getCurrentBranch();

        // Bảo mật Multi-tenant: Kiểm tra phiên ăn có thuộc chi nhánh của nhân viên không
        if (request.getSessionId() != null) {
            DiningSession session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Phiên phục vụ không tồn tại!"));
            if (!"ADMIN".equals(currentRole) && currentBranch != null && currentBranch > 0 && !session.getBranchId().equals(currentBranch)) {
                throw new RuntimeException("Bạn không có quyền thêm món vào phiên phục vụ của chi nhánh khác!");
            }
        }

        OrderItem newItem = new OrderItem();
        newItem.setProductName(request.getProductName());
        newItem.setQuantity(request.getQuantity());
        newItem.setPrice(request.getPrice());

        Long branchId = (!"ADMIN".equals(currentRole) && currentBranch != null && currentBranch > 0)
                ? currentBranch
                : (request.getBranchId() != null ? request.getBranchId() : 1L);
        newItem.setBranchId(branchId);

        newItem.setCustomization(request.getCustomization());
        newItem.setSessionId(request.getSessionId());

        return orderItemRepository.save(newItem);
    }

    public List<OrderItem> getItemsBySession(Long sessionId) {
        String currentRole = TenantContext.getCurrentUserRole();
        Long currentBranch = TenantContext.getCurrentBranch();

        if (!"ADMIN".equals(currentRole) && currentBranch != null && currentBranch > 0) {
            DiningSession session = sessionRepository.findById(sessionId).orElse(null);
            if (session != null && !session.getBranchId().equals(currentBranch)) {
                throw new RuntimeException("Bạn không có quyền xem đơn món của chi nhánh khác!");
            }
        }

        return orderItemRepository.findBySessionId(sessionId);
    }

    @Transactional
    public void deleteOrderItem(Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món cần xóa!"));

        String currentRole = TenantContext.getCurrentUserRole();
        Long currentBranch = TenantContext.getCurrentBranch();

        // Kiểm tra phân quyền chi nhánh
        if (!"ADMIN".equals(currentRole) && currentBranch != null && currentBranch > 0 && !item.getBranchId().equals(currentBranch)) {
            throw new RuntimeException("Bạn không có quyền xóa món của chi nhánh khác!");
        }

        // Không cho phép xóa nếu phiên ăn đã thanh toán (CLOSED)
        if (item.getSessionId() != null) {
            DiningSession session = sessionRepository.findById(item.getSessionId()).orElse(null);
            if (session != null && session.getStatus() == com.example.Restaurant.model.SessionStatus.CLOSED) {
                throw new RuntimeException("Không thể xóa món trong phiên phục vụ đã thanh toán!");
            }
        }

        orderItemRepository.delete(item);
    }
}
