package com.example.Restaurant.service;

import com.example.Restaurant.config.TenantContext;
import com.example.Restaurant.model.DiningSession;
import com.example.Restaurant.model.RestaurantTable;
import com.example.Restaurant.model.SessionStatus;
import com.example.Restaurant.model.TableStatus;
import com.example.Restaurant.repository.DiningSessionRepository;
import com.example.Restaurant.repository.RestaurantTableRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private DiningSessionRepository sessionRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private TableService tableService;

    @BeforeEach
    void setUp() {
        TenantContext.setCurrentBranch(1L);
        TenantContext.setCurrentUserRole("STAFF");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void testBookTableSuccess() {
        RestaurantTable table = new RestaurantTable();
        table.setId(1L);
        table.setStatus(TableStatus.AVAILABLE);
        table.setBranchId(1L);

        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(sessionRepository.save(any(DiningSession.class))).thenAnswer(invocation -> {
            DiningSession s = invocation.getArgument(0);
            s.setId(100L);
            return s;
        });

        DiningSession session = tableService.bookTable(1L);

        assertNotNull(session);
        assertEquals(TableStatus.RESERVED, table.getStatus());
        assertEquals(SessionStatus.OPEN, session.getStatus());
        assertEquals(1L, session.getTableId());
        assertEquals(1L, session.getBranchId());

        verify(tableRepository).save(table);
        verify(sessionRepository).save(any(DiningSession.class));
    }

    @Test
    void testBookTableWithCustomerInfo() {
        RestaurantTable table = new RestaurantTable();
        table.setId(1L);
        table.setStatus(TableStatus.AVAILABLE);
        table.setBranchId(1L);

        com.example.Restaurant.dto.BookTableRequest request = new com.example.Restaurant.dto.BookTableRequest();
        request.setCustomerPhone("0901234567");
        request.setCustomerName("Anh Nam");
        request.setGuestCount(4);
        request.setNote("Gần cửa sổ");

        com.example.Restaurant.model.Customer customer = new com.example.Restaurant.model.Customer();
        customer.setId(55L);
        customer.setName("Anh Nam");
        customer.setPhone("0901234567");

        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(customerService.getOrCreateCustomer("0901234567", "Anh Nam", 1L)).thenReturn(customer);
        when(sessionRepository.save(any(DiningSession.class))).thenAnswer(invocation -> {
            DiningSession s = invocation.getArgument(0);
            s.setId(200L);
            return s;
        });

        DiningSession session = tableService.bookTable(1L, request);

        assertNotNull(session);
        assertEquals(55L, session.getCustomerId());
        assertEquals(4, session.getGuestCount());
        assertEquals("Gần cửa sổ", session.getNote());
        assertEquals(TableStatus.RESERVED, table.getStatus());
    }

    @Test
    void testBookTableCrossBranchForbidden() {
        RestaurantTable tableBranch2 = new RestaurantTable();
        tableBranch2.setId(2L);
        tableBranch2.setStatus(TableStatus.AVAILABLE);
        tableBranch2.setBranchId(2L); // Table belongs to Branch 2, but staff is in Branch 1

        when(tableRepository.findById(2L)).thenReturn(Optional.of(tableBranch2));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tableService.bookTable(2L));
        assertTrue(ex.getMessage().contains("chi nhánh khác"));
    }

    @Test
    void testBookTableNotAvailable() {
        RestaurantTable table = new RestaurantTable();
        table.setId(1L);
        table.setStatus(TableStatus.OCCUPIED);
        table.setBranchId(1L);

        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tableService.bookTable(1L));
        assertEquals("Bàn này không còn trống hoặc đã có người đặt!", ex.getMessage());
    }

    @Test
    void testBookTableNotFound() {
        when(tableRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> tableService.bookTable(999L));
        assertEquals("Không tìm thấy bàn hợp lệ", ex.getMessage());
    }
}
