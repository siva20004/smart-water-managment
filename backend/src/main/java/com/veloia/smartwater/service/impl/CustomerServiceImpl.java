package com.veloia.smartwater.service.impl;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.customer.CustomerRequest;
import com.veloia.smartwater.dto.customer.CustomerResponse;
import com.veloia.smartwater.entity.Customer;
import com.veloia.smartwater.exception.BusinessValidationException;
import com.veloia.smartwater.exception.DuplicateResourceException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.EntityDtoMapper;
import com.veloia.smartwater.repository.CustomerRepository;
import com.veloia.smartwater.service.CustomerService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final EntityDtoMapper mapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, EntityDtoMapper mapper) {
        this.customerRepository = customerRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating new customer with number: {}", request.getCustomerNumber());
        if (customerRepository.existsByCustomerNumber(request.getCustomerNumber())) {
            throw new DuplicateResourceException("Customer", "customerNumber", request.getCustomerNumber());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank() && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", request.getEmail());
        }

        Customer entity = mapper.toCustomerEntity(request);
        Customer saved = customerRepository.save(entity);
        log.info("Customer created with ID: {}", saved.getId());
        return mapper.toCustomerResponse(saved);
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return mapper.toCustomerResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByNumber(String customerNumber) {
        Customer customer = customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "customerNumber", customerNumber));
        return mapper.toCustomerResponse(customer);
    }

    @Override
    public PageResponse<CustomerResponse> getAllCustomers(String search, String type, String status, Pageable pageable) {
        Specification<Customer> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("fullName")), pattern);
                Predicate numberPredicate = cb.like(cb.lower(root.get("customerNumber")), pattern);
                Predicate emailPredicate = cb.like(cb.lower(root.get("email")), pattern);
                predicates.add(cb.or(namePredicate, numberPredicate, emailPredicate));
            }

            if (type != null && !type.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("customerType")), type.trim().toUpperCase()));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Customer> page = customerRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(mapper::toCustomerResponse));
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        log.info("Updating customer ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        if (!customer.getCustomerNumber().equalsIgnoreCase(request.getCustomerNumber())
                && customerRepository.existsByCustomerNumber(request.getCustomerNumber())) {
            throw new DuplicateResourceException("Customer", "customerNumber", request.getCustomerNumber());
        }

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(customer.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", request.getEmail());
        }

        mapper.updateCustomerEntity(customer, request);
        Customer updated = customerRepository.save(customer);
        return mapper.toCustomerResponse(updated);
    }

    @Override
    @Transactional
    public CustomerResponse setCustomerStatus(Long id, String status) {
        log.info("Updating customer status [ID: {}, Status: {}]", id, status);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        customer.setStatus(status.trim().toUpperCase());
        Customer updated = customerRepository.save(customer);
        return mapper.toCustomerResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting or deactivating customer ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        if (customer.getMeters() != null && !customer.getMeters().isEmpty()) {
            throw new BusinessValidationException("Cannot delete customer with assigned active water meters. Please reassign or deactivate meters first.");
        }

        customerRepository.delete(customer);
    }
}
