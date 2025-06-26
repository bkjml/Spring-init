package se.mathias.microservices.core.product.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.mathias.api.core.product.Product;
import se.mathias.api.core.product.ProductService;
import se.mathias.api.exceptions.InvalidInputException;
import se.mathias.api.exceptions.NotFoundException;
import se.mathias.util.http.ServiceUtil;


/**
 * The type Product service.
 */
@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;

    /**
     * Instantiates a new Product service.
     *
     * @param serviceUtil the service util
     */
    @Autowired
    public ProductServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Product getProduct(int productId) {
        LOG.debug("/product return the found product for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        if (productId == 13) {
            throw new NotFoundException("No product found for productId: " + productId);
        }

        return new Product(productId, "name-" + productId, 123, serviceUtil.getServiceAddress());
    }
}
