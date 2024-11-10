package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.bootstrap.BootstrapData;
import guru.springframework.spring6reactive.config.DatabaseConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.test.StepVerifier;

import static guru.springframework.spring6reactive.helper.BeerHelperUtil.getTestBeer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataR2dbcTest(properties = { "spring.datasource.url=jdbc:h2:mem:BeerRepositoryTest;DB_CLOSE_ON_EXIT=TRUE" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import({DatabaseConfig.class, BootstrapData.class})
@Disabled
class BeerRepositoryTest {
    
    @Autowired
    BeerRepository beerRepository;
    
    @Autowired
    ReactiveTransactionManager reactiveTransactionManager;
    
    @Test
    void testSave() {
        beerRepository.save(getTestBeer())
            .as(publisher -> StepVerifier.create(publisher))
            .assertNext(beer -> {
                System.out.println(beer.toString());
                assertEquals("Test Beer", beer.getBeerName());
                assertNotNull( beer.getId());
                assertNotNull(beer.getCreatedDate());
                assertNotNull(beer.getLastModifiedDate());
            })
            .verifyComplete();
    }

    @Test
    void testSave2() {
        StepVerifier.create(TransactionalOperator.create(reactiveTransactionManager)
            .execute(status -> {
                status.setRollbackOnly();
                return beerRepository.save(getTestBeer());
            })).expectNextMatches(savedBeer -> {
                
                assertEquals("Test Beer", savedBeer.getBeerName());
                assertNotNull( savedBeer.getId());
                assertNotNull(savedBeer.getCreatedDate());
                assertNotNull(savedBeer.getLastModifiedDate());
                
                return savedBeer.getBeerName().equals("Test Beer");
            }).verifyComplete();
    }

    @Test
    void testSave3() {
        StepVerifier.create(TransactionalOperator.create(reactiveTransactionManager)
            .execute(status -> {
                status.setRollbackOnly();
                return beerRepository.save(getTestBeer());
            })).assertNext(savedBeer -> {

            assertEquals("Test Beer", savedBeer.getBeerName());
            assertNotNull( savedBeer.getId());
            assertNotNull(savedBeer.getCreatedDate());
            assertNotNull(savedBeer.getLastModifiedDate());

        }).verifyComplete();
    }
}
