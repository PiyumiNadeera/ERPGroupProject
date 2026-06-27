package enterpriseapplication.productservice.service.impl;

import enterpriseapplication.productservice.dto.ProductRequest;
import enterpriseapplication.productservice.dto.ProductResponse;
import enterpriseapplication.productservice.entity.Product;
import enterpriseapplication.productservice.exception.ProductNotFoundException;
import enterpriseapplication.productservice.repository.ProductRepository;
import enterpriseapplication.productservice.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setUnitPrice(request.getUnitPrice());
        product.setStock(request.getStock());

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return toResponse(product);
    }

    @Override
    public void deleteProductById(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
        productRepository.deleteById(productId);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getUnitPrice(),
                product.getStock());
    }
}
