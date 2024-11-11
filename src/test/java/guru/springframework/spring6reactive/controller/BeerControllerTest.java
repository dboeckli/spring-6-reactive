package guru.springframework.spring6reactive.controller;

import guru.springframework.spring6reactive.dto.BeerDto;
import guru.springframework.spring6reactive.model.Beer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static guru.springframework.spring6reactive.helper.BeerHelperUtil.getTestBeer;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureWebTestClient
class BeerControllerTest {
    
    @Autowired
    WebTestClient webTestClient;

    @Test
    @Order(1)
    void testGetBeerById() {
      
        webTestClient.get().uri(BeerController.BEER_PATH_ID, 1)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("content-type", "application/json")
            //.expectBody(BeerDto.class).isEqualTo(BeerDto.builder().beerName("Galaxy Cat").build())
            .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.beerName").isEqualTo("Galaxy Cat")
                .jsonPath("$.beerStyle").isEqualTo("Pale Ale")
                .jsonPath("$.upc").isEqualTo("12356")
                .jsonPath("$.price").isEqualTo(13.0)  
                .jsonPath("$.quantityOnHand").isEqualTo(122);
        
    }

    @Test
    @Order(2)
    void testListBeers() {
        webTestClient.get().uri(BeerController.BEER_PATH)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("content-type", "application/json")
            //.expectBody().jsonPath("$.length()").isEqualTo(3)
            .expectBodyList(BeerDto.class).hasSize(3)
            ;
    }

    @Test
    @Order(3)
    void testCreateBeer() {
        webTestClient.post().uri(BeerController.BEER_PATH)
            .bodyValue(getTestBeer())
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isCreated()
            //.expectHeader().location("http://localhost:8080/api/v2/beer/4")
            .expectHeader().valueMatches("location", "http://localhost:8080/api/v2/beer/\\d+$");
    }

    @Test
    @Order(4)
    void testCreateBeerNameTooShort() {
        webTestClient.post().uri(BeerController.BEER_PATH)
            .bodyValue(BeerDto.builder().beerName("N").build())
            .exchange()
            .expectHeader().valueEquals("content-type", "application/json")
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(5)
    void updateBeer() {
        Beer beerToUpdate = getTestBeer();
        beerToUpdate.setBeerName("New Name");
        
        webTestClient.put().uri(BeerController.BEER_PATH_ID, 1)
            .bodyValue(beerToUpdate)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(6)
    void testPatchBeer() {
        Beer beerToPatch = getTestBeer();
        beerToPatch.setBeerName("New Name");

        webTestClient.patch().uri(BeerController.BEER_PATH_ID, 1)
            .bodyValue(beerToPatch)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(999)
    void deleteBeer() {
        webTestClient.delete().uri(BeerController.BEER_PATH_ID, 1)
            .exchange()
            .expectStatus().isNoContent();
    }
}
