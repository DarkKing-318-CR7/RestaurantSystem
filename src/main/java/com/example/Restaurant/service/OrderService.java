package com.example.Restaurant.service;

import com.example.Restaurant.dto.OrderItemRequest;
import com.example.Restaurant.model.OrderItem;
import com.example.Restaurant.repository.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
        newItem.setBranchId(request.getBranchId());

        //đẩy thẳng map của java cho jsonb
        newItem.setCustomization(request.getCustomization());

        newItem.setSessionId(request.getSessionId());

        return orderItemRepository.save(newItem);
    }
}
