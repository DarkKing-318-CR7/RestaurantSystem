package com.example.Restaurant.service;

import com.example.Restaurant.config.TenantContext;
import com.example.Restaurant.dto.BillResponse;
import com.example.Restaurant.model.*;
import com.example.Restaurant.repository.CustomerRepository;
import com.example.Restaurant.repository.DiningSessionRepository;
import com.example.Restaurant.repository.OrderItemRepository;
import com.example.Restaurant.repository.RestaurantTableRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private DiningSessionRepository sessionRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CheckoutService checkoutService;

    private DiningSession openSession;
    private RestaurantTable table;

    @BeforeEach
    void setUp() {
        TenantContext.setCurrentBranch(1L);
        TenantContext.setCurrentUserRole("STAFF");

        openSession = new DiningSession();
        openSession.setId(10L);
        openSession.setTableId(1L);
        openSession.setBranchId(1L);
        openSession.setStatus(SessionStatus.OPEN);
        openSession.setStartTime(LocalDateTime.now().minusHours(1));

        table = new RestaurantTable();
        table.setId(1L);
        table.setTableNumber("T-01");
        table.setStatus(TableStatus.RESERVED);
        table.setBranchId(1L);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void testProcessCheckoutWalkInSuccess() {
        OrderItem item1 = new OrderItem();
        item1.setPrice(50000.0);
        item1.setQuantity(2); // 100,000

        OrderItem item2 = new OrderItem();
        item2.setPrice(30000.0);
        item2.setQuantity(1); // 30,000

        when(sessionRepository.findById(10L)).thenReturn(Optional.of(openSession));
        when(orderItemRepository.findBySessionId(10L)).thenReturn(Arrays.asList(item1, item2));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));

        BillResponse bill = checkoutService.processCheckout(10L, 1L);

        assertNotNull(bill);
        assertEquals(10L, bill.getSessionId());
        assertEquals("T-01", bill.getTableNumber());
        assertEquals(130000.0, bill.getTotalAmount());
        assertEquals("Khách vãng lai", bill.getCustomerName());
        assertEquals(0, bill.getLoyaltyPointsEarned());
        assertEquals(SessionStatus.CLOSED, openSession.getStatus());
        assertNotNull(openSession.getEndTime());
        assertEquals(TableStatus.AVAILABLE, table.getStatus());

        verify(sessionRepository).save(openSession);
        verify(tableRepository).save(table);
    }

    @Test
    void testProcessCheckoutWithCustomerLoyaltyPoints() {
        openSession.setCustomerId(5L);

        Customer customer = new Customer();
        customer.setId(5L);
        customer.setName("Chị Linh");
        customer.setPhone("0912345678");
        customer.setLoyaltyPoints(10);

        OrderItem item1 = new OrderItem();
        item1.setPrice(100000.0);
        item1.setQuantity(2); // 200,000 -> 20 points

        when(sessionRepository.findById(10L)).thenReturn(Optional.of(openSession));
        when(orderItemRepository.findBySessionId(10L)).thenReturn(Arrays.asList(item1));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(customerRepository.findById(5L)).thenReturn(Optional.of(customer));

        BillResponse bill = checkoutService.processCheckout(10L, 1L);

        assertNotNull(bill);
        assertEquals("Chị Linh", bill.getCustomerName());
        assertEquals("0912345678", bill.getCustomerPhone());
        assertEquals(20, bill.getLoyaltyPointsEarned());
        assertEquals(30, bill.getTotalLoyaltyPoints()); // 10 + 20
        assertEquals(30, customer.getLoyaltyPoints());

        verify(customerRepository).save(customer);
    }

    @Test
    void testProcessCheckoutCrossBranchForbidden() {
        openSession.setBranchId(2L); // Session belongs to branch 2, staff is in branch 1
        when(sessionRepository.findById(10L)).thenReturn(Optional.of(openSession));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> checkoutService.processCheckout(10L, 1L));
        assertTrue(ex.getMessage().contains("chi nhánh khác"));
    }

    @Test
    void testProcessCheckoutAlreadyClosed() {
        openSession.setStatus(SessionStatus.CLOSED);
        when(sessionRepository.findById(10L)).thenReturn(Optional.of(openSession));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> checkoutService.processCheckout(10L, 1L));
        assertEquals("Phiên phục vụ này đã được thanh toán!", exception.getMessage());
    }

    @Test
    void testProcessCheckoutSessionNotFound() {
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> checkoutService.processCheckout(999L, 1L));
        assertEquals("Không tìm thấy phiên phục vụ!", exception.getMessage());
    }
}
