package guru.springframework.spring6reactive.helper;

import guru.springframework.spring6reactive.model.Beer;

import java.math.BigDecimal;

public class BeerHelperUtil {

    public static Beer getTestBeer() {
        return Beer.builder()
            .beerName("Test Beer")
            .beerStyle("IPA")
            .price(BigDecimal.valueOf(5.0))
            .quantityOnHand(3)
            .upc("1234567890123")
            .build();
    }
    
}
