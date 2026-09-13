package com.example.Restaurant.service;

import com.example.Restaurant.dto.CustomerRequest;
import com.example.Restaurant.model.Customer;
import com.example.Restaurant.repository.CustomerRepository;
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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        existingCustomer = new Customer();
        existingCustomer.setId(1L);
        existingCustomer.setName("Anh Nam");
        existingCustomer.setPhone("0901234567");
        existingCustomer.setLoyaltyPoints(50);
        existingCustomer.setBranchId(1L);
    }

    @Test
    void testFindByPhoneFound() {
        when(customerRepository.findByPhone("0901234567")).thenReturn(Optional.of(existingCustomer));

        Optional<Customer> result = customerService.findByPhone("0901234567");
        assertTrue(result.isPresent());
        assertEquals("Anh Nam", result.get().getName());
        assertEquals(50, result.get().getLoyaltyPoints());
    }

    @Test
    void testGetOrCreateCustomerExisting() {
        when(customerRepository.findByPhone("0901234567")).thenReturn(Optional.of(existingCustomer));

        Customer customer = customerService.getOrCreateCustomer("0901234567", "Anh Nam", 1L);
        assertNotNull(customer);
        assertEquals(1L, customer.getId());
        assertEquals("Anh Nam", customer.getName());
    }

    @Test
    void testGetOrCreateCustomerNew() {
        when(customerRepository.findByPhone("0988888888")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(2L);
            return c;
        });

        Customer customer = customerService.getOrCreateCustomer("0988888888", "Chị Hoa", 1L);
        assertNotNull(customer);
        assertEquals(2L, customer.getId());
        assertEquals("Chị Hoa", customer.getName());
        assertEquals("0988888888", customer.getPhone());
        assertEquals(0, customer.getLoyaltyPoints());
    }

    @Test
    void testCreateCustomerDuplicatePhone() {
        CustomerRequest request = new CustomerRequest();
        request.setPhone("0901234567");
        request.setName("Anh Nam Duplicate");

        when(customerRepository.existsByPhone("0901234567")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(request, 1L));
        assertTrue(ex.getMessage().contains("đã tồn tại"));
    }

    @Test
    void testAddPoints() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer updated = customerService.addPoints(1L, 25);
        assertEquals(75, updated.getLoyaltyPoints());
        verify(customerRepository).save(existingCustomer);
    }
}
