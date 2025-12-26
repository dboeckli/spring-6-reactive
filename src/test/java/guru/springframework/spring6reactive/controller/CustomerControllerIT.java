package guru.springframework.spring6reactive.controller;

import guru.springframework.spring6reactive.dto.CustomerDto;
import guru.springframework.spring6reactive.model.Customer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static guru.springframework.spring6reactive.config.SecurityConfig.READ_SCOPE;
import static guru.springframework.spring6reactive.config.SecurityConfig.WRITE_SCOPE;
import static guru.springframework.spring6reactive.helper.TestDataHelperUtil.getTestCustomer;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles(value = "test")
class CustomerControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    @Order(1)
    void testGetCustomerById() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(READ_SCOPE)))
            .get().uri(CustomerController.CUSTOMER_PATH_ID, 1)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("content-type", "application/json")
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.customerName").isEqualTo("Hans");
    }

    @Test
    @Order(1)
    void testGetCustomerByIdNotFound() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(READ_SCOPE)))
            .get().uri(CustomerController.CUSTOMER_PATH_ID, 99)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(2)
    void testListCustomers() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(READ_SCOPE)))
            .get().uri(CustomerController.CUSTOMER_PATH)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("content-type", "application/json")
            //.expectBody().jsonPath("$.length()").isEqualTo(3)
            .expectBodyList(CustomerDto.class).hasSize(4);
    }

    @Test
    @Order(3)
    void testCreateCustomer() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .post().uri(CustomerController.CUSTOMER_PATH)
            .bodyValue(getTestCustomer())
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().valueMatches("location", "http://localhost:8080/api/v2/customer/\\d+$");
    }

    @Test
    @Order(4)
    void testCreateCustomerNameTooShort() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .post().uri(CustomerController.CUSTOMER_PATH)
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

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .put().uri(CustomerController.CUSTOMER_PATH_ID, 1)
            .bodyValue(customerToUpdate)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(5)
    void testUpdateCustomerNotFound() {
        Customer customerToUpdate = getTestCustomer();
        customerToUpdate.setCustomerName("New Name");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .put().uri(CustomerController.CUSTOMER_PATH_ID, 888)
            .bodyValue(customerToUpdate)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testUpdateCustomerNameToShort() {
        Customer customerToPatch = getTestCustomer();
        customerToPatch.setCustomerName("N");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .put().uri(CustomerController.CUSTOMER_PATH_ID, 1)
            .bodyValue(customerToPatch)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @Order(6)
    void testPatchCustomer() {
        Customer customerToPatch = getTestCustomer();
        customerToPatch.setCustomerName("Patched Customer Name");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .patch().uri(CustomerController.CUSTOMER_PATH_ID, 1)
            .bodyValue(customerToPatch)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    @Order(6)
    void testPatchCustomerNotFound() {
        Customer customerToPatch = getTestCustomer();
        customerToPatch.setCustomerName("New Name");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .patch().uri(CustomerController.CUSTOMER_PATH_ID, 777)
            .bodyValue(customerToPatch)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    @Order(6)
    void testPatchCustomerNameTooShort() {
        Customer customerToPatch = getTestCustomer();
        customerToPatch.setCustomerName("N");

        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .patch().uri(CustomerController.CUSTOMER_PATH_ID, 777)
            .bodyValue(customerToPatch)
            .header("content-type", "application/json")
            .exchange()
            .expectStatus().isBadRequest();
    }


    @Test
    @Order(999)
    void testDeleteCustomer() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .delete().uri(CustomerController.CUSTOMER_PATH_ID, 1)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    @Order(999)
    void deleteCustomerNotFound() {
        webTestClient
            .mutateWith(mockJwt().authorities(new SimpleGrantedAuthority(WRITE_SCOPE)))
            .delete().uri(CustomerController.CUSTOMER_PATH_ID, 999)
            .exchange()
            .expectStatus().isNotFound();
    }
}
