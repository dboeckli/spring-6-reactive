package guru.springframework.spring6reactive.helper;

import guru.springframework.spring6reactive.model.Beer;
import guru.springframework.spring6reactive.model.Customer;

import java.math.BigDecimal;

public class TestDataHelperUtil {

    public static Beer getTestBeer() {
        return Beer.builder()
            .beerName("Test Beer")
            .beerStyle("IPA")
            .price(BigDecimal.valueOf(5.0))
            .quantityOnHand(3)
            .upc("1234567890123")
            .build();
    }

    public static Customer getTestCustomer() {
        return Customer.builder()
            .customerName("Test Customer")
            .build();
    }
    
}
