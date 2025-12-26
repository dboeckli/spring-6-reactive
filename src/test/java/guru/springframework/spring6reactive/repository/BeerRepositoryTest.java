package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.bootstrap.BootstrapData;
import guru.springframework.spring6reactive.config.DatabaseConfig;
import guru.springframework.spring6reactive.model.Beer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.test.StepVerifier;

import static guru.springframework.spring6reactive.helper.TestDataHelperUtil.getTestBeer;
import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Import({DatabaseConfig.class, BootstrapData.class})
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(value = "test")
class BeerRepositoryTest {
    
    @Autowired
    BeerRepository beerRepository;
    
    @Autowired
    ReactiveTransactionManager reactiveTransactionManager;
    
    @Test
    @Order(1)
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

        beerRepository.findAll().subscribe(beer -> log.info("### testSave2: " + beer));

    }

    @Test
    @Order(2)
    void testSave3() {
        Beer newBeer = getTestBeer();
        newBeer.setBeerName("New Name 3");
        
        StepVerifier.create(TransactionalOperator.create(reactiveTransactionManager)
            .execute(reactiveTransaction -> {
                reactiveTransaction.setRollbackOnly(); // is rollbacked after save
                return beerRepository.save(newBeer);
            })).assertNext(savedBeer -> {

            assertEquals("New Name 3", savedBeer.getBeerName());
            assertNotNull( savedBeer.getId());
            assertNotNull(savedBeer.getCreatedDate());
            assertNotNull(savedBeer.getLastModifiedDate());

        }).verifyComplete();

        beerRepository.findAll().subscribe(beer -> log.info("### testSave3: " + beer));
    }

    @Test
    @Order(3)
    // In this test, the transaction is not rolled back, so the beer is persisted...
    void testSave() {
        Beer newBeer = getTestBeer();
        newBeer.setBeerName("New Name 1");

        beerRepository.save(newBeer)
            .as(StepVerifier::create)
            .assertNext(beer -> {
                assertEquals("New Name 1", beer.getBeerName());
                assertNotNull( beer.getId());
                assertNotNull(beer.getCreatedDate());
                assertNotNull(beer.getLastModifiedDate());
            })
            .verifyComplete();

        beerRepository.findAll().subscribe(beer -> log.info("### testSave: " + beer));
    }
    
    @Test
    @Order(4)
    /* 
    ...this test is related to the above test (testSave). we need to delete the beer because the created beer has not been rolled back. 
    no rollback happens here neither 
    */
    void testDelete() {
        assertNotNull(beerRepository.findByBeerName("New Name 1").block());
        StepVerifier.create(beerRepository.deleteByBeerName("New Name 1"))
            .verifyComplete();
        assertNull(beerRepository.findByBeerName("New Name 1").block());
    }
}
