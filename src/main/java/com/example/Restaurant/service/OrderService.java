package com.example.Restaurant.service;

import com.example.Restaurant.config.TenantContext;
import com.example.Restaurant.dto.OrderItemRequest;
import com.example.Restaurant.model.OrderItem;
import com.example.Restaurant.repository.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderItemRepository orderItemRepository;
    public OrderService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public OrderItem addDishOrder(OrderItemRequest request) {
        OrderItem newItem = new OrderItem();
        newItem.setProductName(request.getProductName());
        newItem.setQuantity(request.getQuantity());
        newItem.setPrice(request.getPrice());

        Long branchId = request.getBranchId() != null ? request.getBranchId() : TenantContext.getCurrentBranch();
        if (branchId == null) {
            branchId = 1L;
        }
        newItem.setBranchId(branchId);

        //đẩy thẳng map của java cho jsonb
        newItem.setCustomization(request.getCustomization());

        newItem.setSessionId(request.getSessionId());

        return orderItemRepository.save(newItem);
    }

    public List<OrderItem> getItemsBySession(Long sessionId) {
        return orderItemRepository.findBySessionId(sessionId);
    }
}
