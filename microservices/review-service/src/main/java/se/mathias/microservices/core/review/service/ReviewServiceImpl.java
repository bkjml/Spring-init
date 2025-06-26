package se.mathias.microservices.core.review.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.mathias.api.core.review.Review;
import se.mathias.api.core.review.ReviewService;
import se.mathias.api.exceptions.InvalidInputException;
import se.mathias.api.exceptions.NotFoundException;
import se.mathias.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Review service.
 */
@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;

    /**
     * Instantiates a new Review service.
     *
     * @param serviceUtil the service util
     */
    @Autowired
    public ReviewServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Review> getReviews(int productId) {

        if(productId < 1){
            LOG.warn("Invalid input exception");
            throw new InvalidInputException("Invalid Input Exception {}" + productId);
        }if(productId == 213){
            LOG.debug("No reviews found for productId: {}", productId);
            return new ArrayList<>();
        }

        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
        reviews.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
        reviews.add(new Review(productId, 2, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));

        LOG.debug("reviews response size: {}", reviews.size());

        return reviews;
    }
}
