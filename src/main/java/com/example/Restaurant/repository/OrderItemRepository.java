package com.example.Restaurant.repository;

import com.example.Restaurant.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    @Query(value = "SELECT * FROM order_items WHERE customization ->> 'muc_do_cay' = :spiceLevel", nativeQuery = true)
    List<OrderItem> findBySpiceLevel(@Param("spiceLevel") String spiceLevel);

    //tự đôngh generate
    List<OrderItem> findBySessionId(Long sessionId);
}
