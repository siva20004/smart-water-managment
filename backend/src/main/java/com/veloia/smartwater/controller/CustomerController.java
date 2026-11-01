package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.customer.CustomerRequest;
import com.veloia.smartwater.dto.customer.CustomerResponse;
import com.veloia.smartwater.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Water utility subscriber accounts, registration, profile editing, and status management")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(summary = "Register a new utility customer account")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse created = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Customer created successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get paginated customers with search, type, and status filters")
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "customerNumber", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<CustomerResponse> page = customerService.getAllCustomers(search, type, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer details by ID")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable Long id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    @GetMapping("/number/{customerNumber}")
    @Operation(summary = "Get customer details by customer number")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByNumber(@PathVariable String customerNumber) {
        CustomerResponse customer = customerService.getCustomerByNumber(customerNumber);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer account information")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        CustomerResponse updated = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate or deactivate a customer account")
    public ResponseEntity<ApiResponse<CustomerResponse>> setCustomerStatus(
            @PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "ACTIVE");
        CustomerResponse updated = customerService.setCustomerStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Customer status changed to " + status, updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete or archive a customer account")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
    }
}
