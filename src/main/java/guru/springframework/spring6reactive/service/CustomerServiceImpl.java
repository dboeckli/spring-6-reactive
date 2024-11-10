package guru.springframework.spring6reactive.service;

import guru.springframework.spring6reactive.dto.CustomerDto;
import guru.springframework.spring6reactive.mapper.CustomerMapper;
import guru.springframework.spring6reactive.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    
    private final CustomerMapper customerMapper;
    
    @Override
    public Flux<CustomerDto> listCustomers() {
        return customerRepository.findAll().map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId).map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> saveNewCustomer(CustomerDto customerDto) {
        return customerRepository.save(customerMapper.customerDtoToCustomer(customerDto))
            .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> updateCustomer(Integer customerId, CustomerDto customerDto) {
        return customerRepository.findById(customerId)
            .map(foundCustomer -> {
                foundCustomer.setCustomerName(customerDto.getCustomerName());
                return foundCustomer;
            }).flatMap(customerRepository::save)
            .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<CustomerDto> patchCustomer(Integer customerId, CustomerDto customerDto) {
        return customerRepository.findById(customerId)
            .map(foundCustomer -> {
                if (StringUtils.hasText(customerDto.getCustomerName())) {
                    foundCustomer.setCustomerName(customerDto.getCustomerName());
                }
                return foundCustomer;
            }).flatMap(customerRepository::save)
            .map(customerMapper::customerToCustomerDto);
    }

    @Override
    public Mono<Void> deleteCustomer(Integer customerId) {
        return customerRepository.deleteById(customerId);
    }
}
