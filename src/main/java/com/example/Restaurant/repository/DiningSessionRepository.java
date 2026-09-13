package com.example.Restaurant.repository;

import com.example.Restaurant.model.DiningSession;
import com.example.Restaurant.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiningSessionRepository extends JpaRepository<DiningSession, Long> {
    // Tìm phiên phục vụ theo bàn và trạng thái (lấy phiên mới nhất)
    Optional<DiningSession> findFirstByTableIdAndStatusOrderByIdDesc(Long tableId, SessionStatus status);
    Optional<DiningSession> findByTableIdAndStatus(Long tableId, SessionStatus status);
    Optional<DiningSession> findByIdAndStatus(Long sessionId, SessionStatus status);
    Optional<DiningSession> findById(Long sessionId);
}
