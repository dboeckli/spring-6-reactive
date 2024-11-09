package guru.springframework.spring6reactive.mapper;

import guru.springframework.spring6reactive.dto.CustomerDto;
import guru.springframework.spring6reactive.model.Customer;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDto customerDto);

    CustomerDto customerToCustomerDto(Customer customer);
    
}
