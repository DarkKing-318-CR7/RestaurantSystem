package com.example.Restaurant.service;

import com.example.Restaurant.dto.CustomerRequest;
import com.example.Restaurant.model.Customer;
import com.example.Restaurant.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> findByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return Optional.empty();
        }
        return customerRepository.findByPhone(phone.trim());
    }

    public Optional<Customer> findById(Long customerId) {
        if (customerId == null) {
            return Optional.empty();
        }
        return customerRepository.findById(customerId);
    }

    @Transactional
    public Customer getOrCreateCustomer(String phone, String name, Long branchId) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }

        String cleanPhone = phone.trim();
        return customerRepository.findByPhone(cleanPhone).map(existing -> {
            // Cập nhật tên nếu trước đó chưa có tên và lần này có tên mới
            if ((existing.getName() == null || existing.getName().trim().isEmpty()) && name != null && !name.trim().isEmpty()) {
                existing.setName(name.trim());
                return customerRepository.save(existing);
            }
            return existing;
        }).orElseGet(() -> {
            Customer newCustomer = new Customer();
            newCustomer.setPhone(cleanPhone);
            newCustomer.setName(name != null && !name.trim().isEmpty() ? name.trim() : "Khách hàng " + cleanPhone);
            newCustomer.setLoyaltyPoints(0);
            newCustomer.setBranchId(branchId);
            return customerRepository.save(newCustomer);
        });
    }

    @Transactional
    public Customer createCustomer(CustomerRequest request, Long branchId) {
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Số điện thoại khách hàng không được để trống!");
        }
        String cleanPhone = request.getPhone().trim();
        if (customerRepository.existsByPhone(cleanPhone)) {
            throw new RuntimeException("Số điện thoại " + cleanPhone + " đã tồn tại trong hệ thống!");
        }

        Customer customer = new Customer();
        customer.setName(request.getName() != null ? request.getName().trim() : "Khách hàng " + cleanPhone);
        customer.setPhone(cleanPhone);
        customer.setEmail(request.getEmail());
        customer.setLoyaltyPoints(0);
        customer.setBranchId(branchId);

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer addPoints(Long customerId, int pointsToAdd) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + customerId));

        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        customer.setLoyaltyPoints(currentPoints + pointsToAdd);
        return customerRepository.save(customer);
    }

    public List<Customer> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customerRepository.findAll();
        }
        String cleanKeyword = keyword.trim();
        return customerRepository.findByNameContainingIgnoreCaseOrPhoneContaining(cleanKeyword, cleanKeyword);
    }
}
