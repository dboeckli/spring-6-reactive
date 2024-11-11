package guru.springframework.spring6reactive.controller;

import guru.springframework.spring6reactive.dto.CustomerDto;
import guru.springframework.spring6reactive.model.Customer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static guru.springframework.spring6reactive.helper.TestDataHelperUtil.getTestCustomer;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureWebTestClient
class CustomerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    @Order(1)
    void testGetCustomerById() {
        webTestClient.get().uri(CustomerController.CUSTOMER_PATH_ID, 1)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("content-type", "application/json")
            //.expectBody(BeerDto.class).isEqualTo(BeerDto.builder().beerName("Galaxy Cat").build())
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.customerName").isEqualTo("Hans");
    }

    @Test
    @Order(2)
    void testListCustomers() {
        webTestClient.get().uri(CustomerController.CUSTOMER_PATH)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("content-type", "application/json")
            //.expectBody().jsonPath("$.length()").isEqualTo(3)
            .expectBodyList(CustomerDto.class).hasSize(4);
    }

    @Test
    @Order(3)
    void testCreateCustomer() {
        webTestClient.post().uri(CustomerController.CUSTOMER_PATH)
            .bodyValue(getTestCustomer())
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isCreated()
            //.expectHeader().location("http://localhost:8080/api/v2/beer/4")
            .expectHeader().valueMatches("location", "http://localhost:8080/api/v2/customer/\\d+$");
    }

    @Test
    @Order(4)
    void testCreateCustomerNameTooShort() {
        webTestClient.post().uri(CustomerController.CUSTOMER_PATH)
            .bodyValue(CustomerDto.builder().customerName("N").build())
            .exchange()
            .expectHeader().valueEquals("content-type", "application/json")
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(5)
    void testUpdateCustomer() {
        Customer customerToUpdate = getTestCustomer();
        customerToUpdate.setCustomerName("Updated Customer Name");

        webTestClient.put().uri(CustomerController.CUSTOMER_PATH_ID, 1)
            .bodyValue(customerToUpdate)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(6)
    void testPatchCustomer() {
        Customer customerToPatch = getTestCustomer();
        customerToPatch.setCustomerName("Patched Customer Name");

        webTestClient.patch().uri(CustomerController.CUSTOMER_PATH_ID, 1)
            .bodyValue(customerToPatch)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(999)
    void testDeleteCustomer() {
        webTestClient.delete().uri(CustomerController.CUSTOMER_PATH_ID, 1)
            .exchange()
            .expectStatus().isNoContent();
    }
}