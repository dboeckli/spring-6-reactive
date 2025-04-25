package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.model.Beer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BeerRepository extends ReactiveCrudRepository<Beer, Integer> {
    Mono<Void> deleteByBeerName(String beerName);
    Mono<Beer> findByBeerName(String beerName);
}
