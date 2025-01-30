package guru.springframework.spring6reactive.controller;

import guru.springframework.spring6reactive.dto.BeerDto;
import guru.springframework.spring6reactive.service.BeerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static guru.springframework.spring6reactive.config.OpenApiConfiguration.SECURITY_SCHEME_NAME;

@RestController
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class BeerController {
    
    public static final String BEER_PATH = "/api/v2/beer";

    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final BeerService beerService;

    private static final String BEER_NOT_FOUND_MESSAGE = "Beer not found";
    
    @GetMapping(BEER_PATH_ID)
    Mono<BeerDto> getBeerById(@PathVariable("beerId") Integer beerId) {
        return beerService.getBeerById(beerId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, BEER_NOT_FOUND_MESSAGE)));
    }

    @GetMapping(BEER_PATH)
    Flux<BeerDto> listBeers(){
        return beerService.listBeers();
    }
    
    @PostMapping(BEER_PATH)
    Mono<ResponseEntity<Void>> createBeer(@Validated @RequestBody BeerDto beerDto){
        return beerService.saveNewBeer(beerDto)
            .map(savedDto -> ResponseEntity.created(UriComponentsBuilder
                    .fromUriString("http://localhost:8080/" + BEER_PATH + "/" + savedDto.getId())
                    .build().toUri())
                    .build());
    }

    @PutMapping(BEER_PATH_ID)
    Mono<ResponseEntity<Void>> updateBeer(@PathVariable("beerId") Integer beerId,
                                          @Validated @RequestBody BeerDto beerDto) {
        
        return beerService.updateBeer(beerId, beerDto)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, BEER_NOT_FOUND_MESSAGE)))
            .map(savedDto -> ResponseEntity.ok().build());
    }

    @PatchMapping(value = BEER_PATH_ID)
    Mono<ResponseEntity<Void>> patchBeer(@PathVariable("beerId") Integer beerId, 
                                         @Validated @RequestBody BeerDto beerDto) {
        
        return beerService.patchBeer(beerId, beerDto)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, BEER_NOT_FOUND_MESSAGE)))
            .map(savedDto -> ResponseEntity.ok().build());
    }
    
    @DeleteMapping(BEER_PATH_ID)
    Mono<ResponseEntity<Void>> deleteBeer(@PathVariable("beerId") Integer beerId) {
        return beerService.getBeerById(beerId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, BEER_NOT_FOUND_MESSAGE)))
                .map(beerDto -> beerService.deleteBeer(beerDto.getId()))
                .thenReturn(ResponseEntity.noContent().build());
    }

}
