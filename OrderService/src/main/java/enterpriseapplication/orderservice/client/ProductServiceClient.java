package enterpriseapplication.orderservice.client;

import enterpriseapplication.orderservice.dto.ProductDetails;

public interface ProductServiceClient {

    ProductDetails getProductById(Long productId);
}
