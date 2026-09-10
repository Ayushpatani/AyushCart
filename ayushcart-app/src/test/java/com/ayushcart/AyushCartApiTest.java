package com.ayushcart;

import com.ayushcart.repository.ProductRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Starts the whole application (in-memory H2 + seeded data) and calls the real
 * endpoints through MockMvc, including security and JSON serialization.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AyushCartApiTest {

    @Autowired MockMvc mvc;
    @Autowired ProductRepository productRepository;

    @Test
    void anyoneCanBrowseProducts() throws Exception {
        mvc.perform(get("/api/products").param("search", "earbuds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Wireless Earbuds Pro"));
        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void cartRequiresLogin() throws Exception {
        mvc.perform(get("/api/cart")).andExpect(status().isUnauthorized());
    }

    @Test
    void customersCannotUseAdminEndpoints() throws Exception {
        String token = login("customer@ayushcart.com", "Customer@123");
        mvc.perform(get("/api/admin/stats").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"customer@ayushcart.com\",\"password\":\"nope\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidRegistrationReturnsFieldErrors() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"\",\"email\":\"not-an-email\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void fullShoppingFlow_registerAddToCartCheckoutAndCancel() throws Exception {
        // 1. Register a new customer
        String body = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Flow Tester\",\"email\":\"flow@test.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String auth = "Bearer " + JsonPath.read(body, "$.token");

        // 2. Pick a product and remember its stock
        var product = productRepository.findAll().getFirst();
        int stockBefore = product.getStock();

        // 3. Add 2 to cart
        mvc.perform(post("/api/cart/items").header("Authorization", auth).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + product.getId() + ",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2));

        // 4. Checkout
        String address = """
                {"fullName":"Flow Tester","phone":"9876543210","addressLine":"12 MG Road",
                 "city":"Pune","state":"Maharashtra","pincode":"411001"}""";
        String order = mvc.perform(post("/api/orders").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content(address))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andReturn().getResponse().getContentAsString();
        Integer orderId = JsonPath.read(order, "$.id");

        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(stockBefore - 2);
        mvc.perform(get("/api/cart").header("Authorization", auth))
                .andExpect(jsonPath("$.items.length()").value(0));

        // 5. Cancel -> stock is restored
        mvc.perform(post("/api/orders/" + orderId + "/cancel").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(stockBefore);
    }

    private String login(String email, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }
}
