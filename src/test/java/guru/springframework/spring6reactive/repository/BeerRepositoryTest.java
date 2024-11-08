package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.config.DatabaseConfig;
import guru.springframework.spring6reactive.model.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import(DatabaseConfig.class)
class BeerRepositoryTest {
    
    @Autowired
    BeerRepository beerRepository;

    @Test
    void testSave() {
        beerRepository.save(createBeer())
            .subscribe(beer -> { 
                System.out.println(beer.toString());
                assertEquals("Test Beer", beer.getBeerName());
                assertEquals(1, beer.getId());
                assertNotNull(beer.getCreatedDate());
                assertNotNull(beer.getLastModifiedDate());
            });
    }
    
    private Beer createBeer() {
        return Beer.builder()
            .beerName("Test Beer")
            .beerStyle("IPA")
            .price(BigDecimal.valueOf(5.0))
            .quantityOnHand(3)
            .upc("1234567890123")
            .build();
    }

}
