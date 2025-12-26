package guru.springframework.spring6reactive.controller;

import guru.springframework.spring6reactive.dto.BeerDto;
import guru.springframework.spring6reactive.model.Beer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static guru.springframework.spring6reactive.config.SecurityConfig.READ_SCOPE;
import static guru.springframework.spring6reactive.config.SecurityConfig.WRITE_SCOPE;
import static guru.springframework.spring6reactive.helper.TestDataHelperUtil.getTestBeer;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureWebTestClient
@ActiveProfiles(value = "test")
@Slf4j
class BeerControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    @Order(1)
    void testGetBeerById() {

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(READ_SCOPE)))
            .get().uri(BeerController.BEER_PATH_ID, 1)
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
    @Order(1)
    void testGetBeerByIdNotFound() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(READ_SCOPE)))
            .get().uri(BeerController.BEER_PATH_ID, 99)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(2)
    void testListBeers() {
        List<BeerDto> beerList = webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(READ_SCOPE)))
            .get().uri(BeerController.BEER_PATH)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("content-type", "application/json")
            //.expectBody().jsonPath("$.length()").isEqualTo(3)
            .expectBodyList(BeerDto.class).hasSize(3)
            .returnResult().getResponseBody();

        Assertions.assertNotNull(beerList);
        beerList.forEach(beer -> log.info("#### Beer: " + beer));

    }

    @Test
    @Order(3)
    void testCreateBeer() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .post().uri(BeerController.BEER_PATH)
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
        Beer beerToCreate = getTestBeer();
        beerToCreate.setBeerName("N");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .post().uri(BeerController.BEER_PATH)
            //.bodyValue(BeerDto.builder().beerName("N").build())
            .body(Mono.just(beerToCreate), BeerDto.class)
            .exchange()
            .expectHeader().valueEquals("content-type", "application/json")
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(4)
    void testCreateBeerStyleTooShort() {
        Beer beerToCreate = getTestBeer();
        beerToCreate.setBeerStyle("");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .post().uri(BeerController.BEER_PATH)
            .body(Mono.just(beerToCreate), BeerDto.class)
            .exchange()
            .expectHeader().valueEquals("content-type", "application/json")
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(5)
    void testUpdateBeer() {
        Beer beerToUpdate = getTestBeer();
        beerToUpdate.setBeerName("New Name");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .put().uri(BeerController.BEER_PATH_ID, 1)
            .bodyValue(beerToUpdate)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(5)
    void testUpdateBeerNotFound() {
        Beer beerToUpdate = getTestBeer();
        beerToUpdate.setBeerName("New Name");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .put().uri(BeerController.BEER_PATH_ID, 888)
            .bodyValue(beerToUpdate)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testUpdateBeerBeerNameToShort() {
        Beer beerToUpdate = getTestBeer();
        beerToUpdate.setBeerName("N");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .put().uri(BeerController.BEER_PATH_ID, 1)
            .bodyValue(beerToUpdate)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(6)
    void testPatchBeer() {
        Beer beerToPatch = getTestBeer();
        beerToPatch.setBeerName("New Name");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .patch().uri(BeerController.BEER_PATH_ID, 1)
            .bodyValue(beerToPatch)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(6)
    void testPatchBeerNotFound() {
        Beer beerToPatch = getTestBeer();
        beerToPatch.setBeerName("New Name");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .patch().uri(BeerController.BEER_PATH_ID, 777)
            .bodyValue(beerToPatch)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(6)
    void testPatchBeerNameTooShort() {
        Beer beerToPatch = getTestBeer();
        beerToPatch.setBeerName("N");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .patch().uri(BeerController.BEER_PATH_ID, 1)
            .bodyValue(beerToPatch)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(999)
    void deleteBeer() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .delete().uri(BeerController.BEER_PATH_ID, 1)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(999)
    void deleteBeerNotFound() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .delete().uri(BeerController.BEER_PATH_ID, 999)
            .exchange()
            .expectStatus().isNotFound();
    }
}
