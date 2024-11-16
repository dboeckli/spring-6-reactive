package guru.springframework.spring6reactive.controller;

import guru.springframework.spring6reactive.dto.CustomerDto;
import guru.springframework.spring6reactive.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    public static final String CUSTOMER_PATH = "/api/v2/customer";

    public static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    private final CustomerService customerService;

    @GetMapping(CUSTOMER_PATH_ID)
    Mono<CustomerDto> getCustomerById(@PathVariable("customerId") Integer customerId) {
        return customerService.getCustomerById(customerId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")));
    }

    @GetMapping(CUSTOMER_PATH)
    Flux<CustomerDto> listCustomers(){        
        return customerService.listCustomers();
    }

    @PostMapping(CUSTOMER_PATH)
    Mono<ResponseEntity<Void>> createCustomer(@Validated @RequestBody CustomerDto customerDto){
        return customerService.saveNewCustomer(customerDto)
            .map(savedDto -> ResponseEntity.created(UriComponentsBuilder
                    .fromHttpUrl("http://localhost:8080/" + CUSTOMER_PATH + "/" + savedDto.getId())
                    .build().toUri())
                .build());
    }

    @PutMapping(CUSTOMER_PATH_ID)
    Mono<ResponseEntity<Void>> updateCustomer(@PathVariable("customerId") Integer customerId,
                                              @Validated @RequestBody CustomerDto customerDto) {

        return customerService.updateCustomer(customerId, customerDto)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")))
            .map(savedDto -> ResponseEntity.ok().build());
    }

    @PatchMapping(value = CUSTOMER_PATH_ID)
    Mono<ResponseEntity<Void>> patchCustomer(@PathVariable("customerId") Integer customerId,
                                             @Validated @RequestBody CustomerDto customerDto) {

        return customerService.patchCustomer(customerId, customerDto)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")))
            .map(savedDto -> ResponseEntity.ok().build());
    }

    @DeleteMapping(CUSTOMER_PATH_ID)
    Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable("customerId") Integer customerId) {
        return customerService.getCustomerById(customerId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")))
            .map(customerDto -> customerService.deleteCustomer(customerDto.getId()))
            .thenReturn(ResponseEntity.noContent().build());
    }
    
}
