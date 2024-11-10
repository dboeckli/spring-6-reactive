package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}
