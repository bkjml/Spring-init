package se.mathias.microservices.composite.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import se.mathias.api.core.product.Product;
import se.mathias.api.core.product.ProductService;
import se.mathias.api.core.recommendation.Recommendation;
import se.mathias.api.core.recommendation.RecommendationService;
import se.mathias.api.core.review.Review;
import se.mathias.api.core.review.ReviewService;
import se.mathias.api.exceptions.InvalidInputException;
import se.mathias.api.exceptions.NotFoundException;
import se.mathias.util.http.HttpErrorInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

/**
 * The type Product composite integration.
 */
@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final String productServiceUrl;

    private final String recommendationServiceUrl;

    private final String reviewServiceUrl;

    /**
     * Instantiates a new Product composite integration.
     *
     * @param restTemplate              the rest template
     * @param objectMapper              the object mapper
     * @param productServiceHost        the product service host
     * @param productServicePort        the product service port
     * @param recommendationServiceHost the recommendation service host
     * @param recommendationServicePort the recommendation service port
     * @param reviewServiceHost         the review service host
     * @param reviewServicePort         the review service port
     */
    @Autowired
    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort,
            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int recommendationServicePort,
            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int reviewServicePort) {

        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

        this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        this.recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
    }


    @Override
    public Product getProduct(int productId) {
        try{
            String url = productServiceUrl + productId;
            LOG.debug("Calling getProduct API on url: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);
            LOG.debug("Found a product with id: {}", product.getProductId());

            return product;
        }catch (HttpClientErrorException ex){
            switch (HttpStatus.resolve(ex.getStatusCode().value())){
                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));
                case UNPROCESSABLE_ENTITY:
                    throw new InvalidInputException(getErrorMessage(ex));
                default:
                    LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try{
            String recommendationUrl = recommendationServiceUrl + productId;
            LOG.debug("Calling recommendations API on url: {}", recommendationUrl);

            List<Recommendation> recommendations = restTemplate
                    .exchange(recommendationUrl, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {})
                    .getBody();
            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;
        }catch (Exception ex){
            LOG.warn("Exception occurred while requesting a recommendation for product id: {}, return zero recommendations: {}", productId, ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Review> getReviews(int productId) {
        try{
            String reviewUrl = reviewServiceUrl + productId;
            LOG.debug("Calling review API with url: {}", reviewUrl);

            List<Review> reviews = restTemplate
                    .exchange(reviewUrl, GET, null, new ParameterizedTypeReference<List<Review>>() {})
                    .getBody();
            LOG.debug("Got {} reviews for a product id: {}", reviews.size(), productId);
            return reviews;
        }catch (Exception ex){
            LOG.warn("Got an exception while requesting a review on product id: {}, gor zero reviews: {}", productId, ex.getMessage());
            return new ArrayList<>();
        }
    }

    private String getErrorMessage(HttpClientErrorException ex){
        try{
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        }catch (IOException ioex){
            return ex.getMessage();
        }
    }
}
