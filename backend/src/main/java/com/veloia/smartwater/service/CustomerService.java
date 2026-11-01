package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.customer.CustomerRequest;
import com.veloia.smartwater.dto.customer.CustomerResponse;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomerById(Long id);

    CustomerResponse getCustomerByNumber(String customerNumber);

    PageResponse<CustomerResponse> getAllCustomers(String search, String type, String status, Pageable pageable);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    CustomerResponse setCustomerStatus(Long id, String status);

    void deleteCustomer(Long id);
}
