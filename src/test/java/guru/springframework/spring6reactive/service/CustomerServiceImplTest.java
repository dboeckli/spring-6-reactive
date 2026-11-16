package guru.springframework.spring6reactive.service;

import guru.springframework.spring6reactive.dto.CustomerDto;
import guru.springframework.spring6reactive.mapper.CustomerMapper;
import guru.springframework.spring6reactive.model.Customer;
import guru.springframework.spring6reactive.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void patchCustomerKeepsExistingNameWhenPatchNameIsEmpty() {
        Customer existing = Customer.builder().id(1).customerName("John Doe").build();
        CustomerDto mapped = CustomerDto.builder().customerName("John Doe").build();
        when(customerRepository.findById(1)).thenReturn(Mono.just(existing));
        when(customerRepository.save(existing)).thenReturn(Mono.just(existing));
        when(customerMapper.customerToCustomerDto(existing)).thenReturn(mapped);

        StepVerifier.create(customerService.patchCustomer(1, new CustomerDto())).expectNext(mapped).verifyComplete();

        assertThat(existing.getCustomerName()).isEqualTo("John Doe");
    }

    @Test
    void patchCustomerUpdatesNameWhenProvided() {
        Customer existing = Customer.builder().id(1).customerName("John Doe").build();
        CustomerDto mapped = CustomerDto.builder().customerName("Jane Doe").build();
        when(customerRepository.findById(1)).thenReturn(Mono.just(existing));
        when(customerRepository.save(existing)).thenReturn(Mono.just(existing));
        when(customerMapper.customerToCustomerDto(existing)).thenReturn(mapped);

        StepVerifier.create(customerService.patchCustomer(1, CustomerDto.builder().customerName("Jane Doe").build()))
            .expectNext(mapped)
            .verifyComplete();

        assertThat(existing.getCustomerName()).isEqualTo("Jane Doe");
    }

}
