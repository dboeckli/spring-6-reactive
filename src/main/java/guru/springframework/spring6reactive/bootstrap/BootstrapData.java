package guru.springframework.spring6reactive.bootstrap;

import guru.springframework.spring6reactive.model.Beer;
import guru.springframework.spring6reactive.repository.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Log
@RequiredArgsConstructor
@Component
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;

    @Override
    public void run(String... args) {
        loadBeerData();

        beerRepository.count().subscribe(count -> {
            log.info("Beer data loaded: " + count);
            System.out.println("Count is: " + count);
        });
    }

    private void loadBeerData() {
        beerRepository.count().subscribe(count -> {
            if (count == 0) {
                Beer beer1 = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle("Pale Ale")
                    .upc("12356")
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(122)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

                Beer beer2 = Beer.builder()
                    .beerName("Crank")
                    .beerStyle("Pale Ale")
                    .upc("12356222")
                    .price(new BigDecimal("11.99"))
                    .quantityOnHand(392)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

                Beer beer3 = Beer.builder()
                    .beerName("Sunshine City")
                    .beerStyle("IPA")
                    .upc("12356")
                    .price(new BigDecimal("13.99"))
                    .quantityOnHand(144)
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

                beerRepository.save(beer1).subscribe();
                beerRepository.save(beer2).subscribe();
                beerRepository.save(beer3).subscribe();
            }
        });
    }
}
