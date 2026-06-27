package enterpriseapplication.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import enterpriseapplication.productservice.dto.ProductRequest;
import enterpriseapplication.productservice.dto.ProductResponse;
import enterpriseapplication.productservice.exception.ProductNotFoundException;
import enterpriseapplication.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct_returns201() throws Exception {
        ProductRequest request = new ProductRequest("Laptop", "15-inch laptop", "Electronics", new BigDecimal("999.99"), 10);
        ProductResponse response = new ProductResponse(1L, "Laptop", "15-inch laptop", "Electronics", new BigDecimal("999.99"), 10);
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void createProduct_returns400_whenInvalid() throws Exception {
        ProductRequest invalidRequest = new ProductRequest("", null, null, null, -5);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProductById_returns200_whenFound() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Laptop", "15-inch laptop", "Electronics", new BigDecimal("999.99"), 10);
        when(productService.getProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1L));
    }

    @Test
    void getProductById_returns404_whenNotFound() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProductById_returns204_whenFound() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProductById(1L);
    }

    @Test
    void deleteProductById_returns404_whenNotFound() throws Exception {
        org.mockito.Mockito.doThrow(new ProductNotFoundException(99L)).when(productService).deleteProductById(99L);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());
    }
}
