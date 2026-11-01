package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.customer.CustomerRequest;
import com.veloia.smartwater.dto.customer.CustomerResponse;
import com.veloia.smartwater.entity.Customer;
import com.veloia.smartwater.exception.DuplicateResourceException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.EntityDtoMapper;
import com.veloia.smartwater.repository.CustomerRepository;
import com.veloia.smartwater.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private final EntityDtoMapper mapper = new EntityDtoMapper();

    private CustomerServiceImpl customerService;

    private Customer sampleCustomer;
    private CustomerRequest sampleRequest;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, mapper);

        sampleCustomer = new Customer();
        sampleCustomer.setId(1L);
        sampleCustomer.setCustomerNumber("CUST-TEST-001");
        sampleCustomer.setCustomerType("RESIDENTIAL");
        sampleCustomer.setFullName("Kenji Sato");
        sampleCustomer.setEmail("kenji.sato@example.jp");
        sampleCustomer.setBillingAddress("1-2-3 Shinjuku, Tokyo");
        sampleCustomer.setStatus("ACTIVE");

        sampleRequest = new CustomerRequest();
        sampleRequest.setCustomerNumber("CUST-TEST-001");
        sampleRequest.setCustomerType("RESIDENTIAL");
        sampleRequest.setFullName("Kenji Sato");
        sampleRequest.setEmail("kenji.sato@example.jp");
        sampleRequest.setBillingAddress("1-2-3 Shinjuku, Tokyo");
        sampleRequest.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("createCustomer successfully registers a new customer")
    void createCustomer_Success() {
        when(customerRepository.existsByCustomerNumber("CUST-TEST-001")).thenReturn(false);
        when(customerRepository.existsByEmail("kenji.sato@example.jp")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CustomerResponse result = customerService.createCustomer(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCustomerNumber()).isEqualTo("CUST-TEST-001");
        assertThat(result.getFullName()).isEqualTo("Kenji Sato");
    }

    @Test
    @DisplayName("createCustomer throws DuplicateResourceException on existing customer number")
    void createCustomer_DuplicateNumber_ThrowsException() {
        when(customerRepository.existsByCustomerNumber("CUST-TEST-001")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(sampleRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("CUST-TEST-001");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("getCustomerById returns response when found")
    void getCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        CustomerResponse result = customerService.getCustomerById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("Kenji Sato");
    }

    @Test
    @DisplayName("getCustomerById throws ResourceNotFoundException when missing")
    void getCustomerById_NotFound_ThrowsException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
