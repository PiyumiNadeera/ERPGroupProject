package enterpriseapplication.productservice.service;

import enterpriseapplication.productservice.dto.ProductRequest;
import enterpriseapplication.productservice.dto.ProductResponse;
import enterpriseapplication.productservice.entity.Product;
import enterpriseapplication.productservice.exception.ProductNotFoundException;
import enterpriseapplication.productservice.repository.ProductRepository;
import enterpriseapplication.productservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void createProduct_savesAndReturnsProduct() {
        ProductRequest request = new ProductRequest("Laptop", "15-inch laptop", "Electronics", new BigDecimal("999.99"), 10);
        Product saved = new Product(1L, "Laptop", "15-inch laptop", "Electronics", new BigDecimal("999.99"), 10);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = productService.createProduct(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Laptop");
        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Laptop");
    }

    @Test
    void getProductById_returnsProduct_whenFound() {
        Product product = new Product(1L, "Laptop", "15-inch laptop", "Electronics", new BigDecimal("999.99"), 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Laptop");
    }

    @Test
    void getProductById_throws_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void deleteProductById_deletes_whenFound() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProductById(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProductById_throws_whenNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProductById(99L))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
