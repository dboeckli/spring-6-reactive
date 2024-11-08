package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.model.Beer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BeerRepository extends ReactiveCrudRepository<Beer, Integer> {
}
