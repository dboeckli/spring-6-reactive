package guru.springframework.spring6reactive.repository;

import guru.springframework.spring6reactive.config.DatabaseConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

import static guru.springframework.spring6reactive.helper.BeerHelperUtil.getTestBeer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataR2dbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(DatabaseConfig.class)
@Disabled
class BeerRepositoryTest {
    
    @Autowired
    BeerRepository beerRepository;

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
}
