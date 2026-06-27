package enterpriseapplication.orderservice.client;

import enterpriseapplication.orderservice.dto.ProductDetails;
import enterpriseapplication.orderservice.exception.ProductNotFoundException;
import enterpriseapplication.orderservice.exception.ProductServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductServiceClientImpl implements ProductServiceClient {

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductServiceClientImpl(RestTemplate restTemplate,
                                     @Value("${product-service.base-url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    @Override
    public ProductDetails getProductById(Long productId) {
        String url = productServiceUrl + "/api/products/" + productId;
        try {
            ProductDetails product = restTemplate.getForObject(url, ProductDetails.class);
            if (product == null) {
                throw new ProductNotFoundException(productId);
            }
            return product;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException(productId);
        } catch (RestClientException ex) {
            throw new ProductServiceUnavailableException("Product Service is unavailable", ex);
        }
    }
}
