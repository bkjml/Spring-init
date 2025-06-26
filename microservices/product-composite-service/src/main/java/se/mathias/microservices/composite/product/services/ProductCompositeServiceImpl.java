package se.mathias.microservices.composite.product.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.mathias.api.composite.product.*;
import se.mathias.api.core.product.Product;
import se.mathias.api.core.recommendation.Recommendation;
import se.mathias.api.core.review.Review;
import se.mathias.api.exceptions.NotFoundException;
import se.mathias.util.http.ServiceUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Product composite service.
 */
@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {


    private final ServiceUtil serviceUtil;
    private ProductCompositeIntegration productCompositeIntegration;

    /**
     * Instantiates a new Product composite service.
     *
     * @param serviceUtil                 the service util
     * @param productCompositeIntegration the product composite integration
     */
    @Autowired
    public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration productCompositeIntegration) {
        this.serviceUtil = serviceUtil;
        this.productCompositeIntegration = productCompositeIntegration;
    }

    @Override
    public ProductAggregate getProduct(int productId) {

        Product product = productCompositeIntegration.getProduct(productId);
        if (product == null) {
            throw new NotFoundException("No product found for productId: " + productId);
        }

        List<Recommendation> recommendationList = productCompositeIntegration.getRecommendations(productId);
        List<Review> reviewList = productCompositeIntegration.getReviews(productId);

        return createProductAggregate(product, recommendationList, reviewList, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createProductAggregate(
            Product product,
            List<Recommendation> recommendationList,
            List<Review> reviewList,
            String serviceAddress) {

        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        List<RecommendationSummary> recommendationSummaries =
                (recommendationList == null) ? null : recommendationList.stream()
                        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate()))
                        .collect(Collectors.toList());

        List<ReviewSummary> reviewSummaries =
                (reviewList == null) ? null : reviewList.stream()
                        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
                        .collect(Collectors.toList());


        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviewList != null && reviewList.size() > 0) ? reviewList.get(0).getServiceAddress() : "";
        String recommendationAddress = (recommendationList != null && recommendationList.size() > 0) ? recommendationList.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);

    }
}
