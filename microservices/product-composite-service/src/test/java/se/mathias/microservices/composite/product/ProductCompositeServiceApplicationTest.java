package se.mathias.microservices.composite.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.mathias.api.core.product.Product;
import se.mathias.api.core.recommendation.Recommendation;
import se.mathias.api.core.review.Review;
import se.mathias.api.exceptions.InvalidInputException;
import se.mathias.api.exceptions.NotFoundException;
import se.mathias.microservices.composite.product.services.ProductCompositeIntegration;


import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductCompositeServiceApplicationTest {

    private static final int PRODUCT_ID = 1;
    private static final int PRODUCT_ID_NOT_FOUND = 2;
    private static final int PRODUCT_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private ProductCompositeIntegration productCompositeIntegration;

    @BeforeEach
    void setup(){
        when(productCompositeIntegration.getProduct(PRODUCT_ID))
                .thenReturn(new Product(PRODUCT_ID, "name", 1, "mock-address"));
        when(productCompositeIntegration.getRecommendations(PRODUCT_ID))
                .thenReturn(singletonList(new Recommendation(PRODUCT_ID, 1, "Author", 5, "content", "mock-address")));
        when(productCompositeIntegration.getReviews(PRODUCT_ID))
                .thenReturn(singletonList(new Review(PRODUCT_ID, 1, "Author", "Subject", "content", "mock-address")));

        when(productCompositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
                .thenThrow(new NotFoundException("NOT FOUND: "+PRODUCT_ID_NOT_FOUND));
        when(productCompositeIntegration.getProduct(PRODUCT_ID_INVALID))
                .thenThrow(new InvalidInputException("INVALID: "+PRODUCT_ID_INVALID));
    }


    @Test
    void getProductById(){
        client.get()
                .uri("/product-composite/" + PRODUCT_ID)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.productId").isEqualTo(PRODUCT_ID)
                .jsonPath("$.recommendations.length()").isEqualTo(1)
                .jsonPath("$.reviews.length()").isEqualTo(1);
    }

    @Test
    void getProductNotFound(){
        client.get()
                .uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/product-composite/"+PRODUCT_ID_NOT_FOUND)
                .jsonPath("$.message").isEqualTo("NOT FOUND: "+PRODUCT_ID_NOT_FOUND);
    }

    @Test
    void getProductInvalid(){
        client.get()
                .uri("/product-composite/"+PRODUCT_ID_INVALID)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/product-composite/"+PRODUCT_ID_INVALID)
                .jsonPath("$.message").isEqualTo("INVALID: "+PRODUCT_ID_INVALID);
    }


}