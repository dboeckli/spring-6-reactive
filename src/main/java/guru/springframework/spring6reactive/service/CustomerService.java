package guru.springframework.spring6reactive.service;

import guru.springframework.spring6reactive.dto.CustomerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Flux<CustomerDto> listCustomers();

    Mono<CustomerDto> getCustomerById(Integer customerId);

    Mono<CustomerDto> saveNewCustomer(CustomerDto customerDto);

    Mono<CustomerDto> updateCustomer(Integer customerId, CustomerDto customerDto);

    Mono<CustomerDto> patchCustomer(Integer customerId, CustomerDto customerDto);

    Mono<Void> deleteCustomer(Integer customerId);
}
