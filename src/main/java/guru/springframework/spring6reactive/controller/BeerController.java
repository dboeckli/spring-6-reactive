package guru.springframework.spring6reactive.controller;

import guru.springframework.spring6reactive.dto.BeerDto;
import guru.springframework.spring6reactive.service.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class BeerController {

    public static final String BEER_PATH = "/api/v2/beer";

    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final BeerService beerService;
    
    @GetMapping(BEER_PATH_ID)
    Mono<BeerDto> getBeerById(@PathVariable("beerId") Integer beerId) {
        return beerService.getBeerById(beerId);
    }

    @GetMapping(BEER_PATH)
    Flux<BeerDto> listBeers(){
        return beerService.listBeers();
    }
    
    @PostMapping(BEER_PATH)
    Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto){
        return beerService.saveNewBeer(beerDto)
            .map(savedDto -> ResponseEntity.created(UriComponentsBuilder
                    .fromHttpUrl("http://localhost:8080/" + BEER_PATH + "/" + savedDto.getId())
                    .build().toUri())
                    .build());
    }

    @PutMapping(BEER_PATH_ID)
    ResponseEntity<Void> updateBeer(@PathVariable("beerId") Integer beerId, @RequestBody BeerDto beerDto) {
        beerService.updateBeer(beerId, beerDto).subscribe();
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = BEER_PATH_ID)
    ResponseEntity<Void> patchBeer(@PathVariable("beerId") Integer beerId, @RequestBody BeerDto beerDto) {
        beerService.patchBeer(beerId, beerDto).subscribe();
        return ResponseEntity.ok().build();
    }

}
