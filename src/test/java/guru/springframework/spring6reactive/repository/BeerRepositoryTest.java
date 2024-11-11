package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.bootstrap.BootstrapData;
import guru.springframework.spring6reactive.config.DatabaseConfig;
import guru.springframework.spring6reactive.model.Beer;
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
class BeerRepositoryTest {
    
    @Autowired
    BeerRepository beerRepository;
    
    @Autowired
    ReactiveTransactionManager reactiveTransactionManager;
    
    @Test
    void testSave() {
        Beer newBeer = getTestBeer();
        newBeer.setBeerName("New Name 1");
        
        beerRepository.save(newBeer)
            .as(publisher -> StepVerifier.create(publisher))
            .assertNext(beer -> {
                assertEquals("New Name 1", beer.getBeerName());
                assertNotNull( beer.getId());
                assertNotNull(beer.getCreatedDate());
                assertNotNull(beer.getLastModifiedDate());
            })
            .verifyComplete();
    }

    @Test
    void testSave2() {
        Beer newBeer = getTestBeer();
        newBeer.setBeerName("New Name 2");
        
        StepVerifier.create(TransactionalOperator.create(reactiveTransactionManager)
            .execute(reactiveTransaction -> {
                reactiveTransaction.setRollbackOnly();  // is rollbacked after save
                return beerRepository.save(newBeer);
            })).expectNextMatches(savedBeer -> {
                
                assertEquals("New Name 2", savedBeer.getBeerName());
                assertNotNull( savedBeer.getId());
                assertNotNull(savedBeer.getCreatedDate());
                assertNotNull(savedBeer.getLastModifiedDate());
                
                return savedBeer.getBeerName().equals("New Name 2");
            }).verifyComplete();

    }

    @Test
    void testSave3() {
        Beer newBeer = getTestBeer();
        newBeer.setBeerName("New Name 3");
        
        StepVerifier.create(TransactionalOperator.create(reactiveTransactionManager)
            .execute(status -> {
                status.setRollbackOnly();
                return beerRepository.save(newBeer);
            })).assertNext(savedBeer -> {

            assertEquals("New Name 3", savedBeer.getBeerName());
            assertNotNull( savedBeer.getId());
            assertNotNull(savedBeer.getCreatedDate());
            assertNotNull(savedBeer.getLastModifiedDate());

        }).verifyComplete();
    }
}
