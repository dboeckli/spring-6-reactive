package guru.springframework.spring6reactive.bootstrap;

import guru.springframework.spring6reactive.model.Beer;
import guru.springframework.spring6reactive.model.Customer;
import guru.springframework.spring6reactive.repository.BeerRepository;
import guru.springframework.spring6reactive.repository.CustomerRepository;
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
    
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        loadBeerData();
        loadCustomerData();
        
        log.info("Bootstrapping data");

        beerRepository.count().subscribe(count -> {
            log.info("Beer data loaded: " + count);
        });
        beerRepository.findAll().subscribe(beer -> log.info(beer.toString()));

        customerRepository.count().subscribe(count -> {
            log.info("Customer data loaded: " + count);
        });
        customerRepository.findAll().subscribe(customer -> log.info(customer.toString()));
    }

    private void loadCustomerData() {
        customerRepository.count().subscribe(count -> {
            if (count == 0) {
                Customer customer1 = Customer.builder()
                    .customerName("Hans")
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

                Customer customer2 = Customer.builder()
                    .customerName("Fritz")
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

                Customer customer3 = Customer.builder()
                    .customerName("Peter")
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

                Customer customer4 = Customer.builder()
                    .customerName("Pumukel")
                    .createdDate(LocalDateTime.now())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

                customerRepository.save(customer1).subscribe();
                customerRepository.save(customer2).subscribe();
                customerRepository.save(customer3).subscribe();
                customerRepository.save(customer4).subscribe();
            }
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
