package guru.springframework.spring6reactive.service;

import guru.springframework.spring6reactive.dto.BeerDto;
import guru.springframework.spring6reactive.mapper.BeerMapper;
import guru.springframework.spring6reactive.model.Beer;
import guru.springframework.spring6reactive.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeerServiceImplTest {

    @Mock
    private BeerRepository beerRepository;

    @Mock
    private BeerMapper beerMapper;

    @InjectMocks
    private BeerServiceImpl beerService;

    @Test
    void patchBeerKeepsExistingValuesWhenPatchIsEmpty() {
        Beer existing = Beer.builder()
            .id(1)
            .beerName("Galaxy Cat")
            .beerStyle("Pale Ale")
            .upc("12356")
            .price(BigDecimal.valueOf(13.0))
            .quantityOnHand(122)
            .build();
        BeerDto mapped = BeerDto.builder().beerName("Galaxy Cat").build();
        when(beerRepository.findById(1)).thenReturn(Mono.just(existing));
        when(beerRepository.save(existing)).thenReturn(Mono.just(existing));
        when(beerMapper.beerToBeerDto(existing)).thenReturn(mapped);

        StepVerifier.create(beerService.patchBeer(1, new BeerDto())).expectNext(mapped).verifyComplete();

        assertThat(existing.getBeerName()).isEqualTo("Galaxy Cat");
        assertThat(existing.getBeerStyle()).isEqualTo("Pale Ale");
        assertThat(existing.getUpc()).isEqualTo("12356");
        assertThat(existing.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(13.0));
        assertThat(existing.getQuantityOnHand()).isEqualTo(122);
    }

    @Test
    void patchBeerUpdatesOnlyProvidedValues() {
        Beer existing = Beer.builder()
            .id(1)
            .beerName("Galaxy Cat")
            .beerStyle("Pale Ale")
            .upc("12356")
            .price(BigDecimal.valueOf(13.0))
            .quantityOnHand(122)
            .build();
        BeerDto patch = BeerDto.builder().beerName("New Name").quantityOnHand(7).build();
        BeerDto mapped = BeerDto.builder().beerName("New Name").build();
        when(beerRepository.findById(1)).thenReturn(Mono.just(existing));
        when(beerRepository.save(existing)).thenReturn(Mono.just(existing));
        when(beerMapper.beerToBeerDto(existing)).thenReturn(mapped);

        StepVerifier.create(beerService.patchBeer(1, patch)).expectNext(mapped).verifyComplete();

        assertThat(existing.getBeerName()).isEqualTo("New Name");
        assertThat(existing.getBeerStyle()).isEqualTo("Pale Ale");
        assertThat(existing.getUpc()).isEqualTo("12356");
        assertThat(existing.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(13.0));
        assertThat(existing.getQuantityOnHand()).isEqualTo(7);
    }

}
