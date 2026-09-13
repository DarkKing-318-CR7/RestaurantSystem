package com.example.Restaurant.service;

import com.example.Restaurant.config.TenantContext;
import com.example.Restaurant.model.DiningSession;
import com.example.Restaurant.model.RestaurantTable;
import com.example.Restaurant.model.SessionStatus;
import com.example.Restaurant.model.TableStatus;
import com.example.Restaurant.repository.DiningSessionRepository;
import com.example.Restaurant.repository.RestaurantTableRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TableService {
    private final RestaurantTableRepository restaurantTableRepository;
    private final DiningSessionRepository sessionRepository;

    public TableService(RestaurantTableRepository restaurantTableRepository,  DiningSessionRepository sessionRepository) {
        this .restaurantTableRepository = restaurantTableRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public DiningSession bookTable(Long tableId){
        //tìm bàn trong database
        RestaurantTable table=restaurantTableRepository.findById(tableId)
                .orElseThrow(()->new RuntimeException("Không tìm thấy bàn hợp lệ"));
        if(table.getStatus()!= TableStatus.AVAILABLE){
            throw new RuntimeException("Bàn này không còn trống hoặc đã có người đặt!");
        }

        //3. đổi trạng thái sang đã đặt
        table.setStatus(TableStatus.RESERVED);

        //lưu xuống db
        restaurantTableRepository.save(table);

        //2.mở phiên phục vụ mới
        DiningSession session=new DiningSession();
        session.setTableId(tableId);
        session.setBranchId(TenantContext.getCurrentBranch());
        session.setStatus(SessionStatus.OPEN);

        return sessionRepository.save(session);
    }

}
