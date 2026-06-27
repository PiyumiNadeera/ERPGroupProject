package enterpriseapplication.productservice.service;

import enterpriseapplication.productservice.dto.ProductRequest;
import enterpriseapplication.productservice.dto.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long productId);

    void deleteProductById(Long productId);
}
