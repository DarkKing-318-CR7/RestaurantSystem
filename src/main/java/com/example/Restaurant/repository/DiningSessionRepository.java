package com.example.Restaurant.repository;

import com.example.Restaurant.model.DiningSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiningSessionRepository extends JpaRepository<DiningSession, Long>{
    //Tìm xem khách có đang ngồi bàn này ko
    Optional<DiningSession> findByIdAndStatus(Long sessionId,String status);
    Optional<DiningSession> findById(Long sessionId);
}
