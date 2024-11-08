package guru.springframework.spring6reactive.service;

import guru.springframework.spring6reactive.dto.BeerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerService {
    
    Flux<BeerDto> listBeers();

    Mono<BeerDto> getBeerById(Integer beerId);

    Mono<BeerDto> saveNewBeer(BeerDto beerDto);

    Mono<BeerDto> updateBeer(Integer beerId, BeerDto beerDto);

    Mono<BeerDto> patchBeer(Integer beerId, BeerDto beerDto);
}
