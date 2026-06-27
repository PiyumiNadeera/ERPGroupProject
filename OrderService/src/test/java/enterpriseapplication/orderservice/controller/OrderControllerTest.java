package enterpriseapplication.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import enterpriseapplication.orderservice.dto.OrderRequest;
import enterpriseapplication.orderservice.dto.OrderResponse;
import enterpriseapplication.orderservice.exception.ProductNotFoundException;
import enterpriseapplication.orderservice.exception.ProductServiceUnavailableException;
import enterpriseapplication.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrder_returns201() throws Exception {
        OrderRequest request = new OrderRequest(1L, 10L, 3);
        OrderResponse response = new OrderResponse(1L, 1L, 10L, "Laptop", 3, new BigDecimal("300.00"), LocalDateTime.now(), "CREATED");
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(300.00));
    }

    @Test
    void createOrder_returns400_whenInvalid() throws Exception {
        OrderRequest invalidRequest = new OrderRequest(null, null, -1);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_returns400_whenProductNotFound() throws Exception {
        OrderRequest request = new OrderRequest(1L, 99L, 1);
        when(orderService.createOrder(any(OrderRequest.class))).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_returns503_whenProductServiceUnavailable() throws Exception {
        OrderRequest request = new OrderRequest(1L, 10L, 1);
        when(orderService.createOrder(any(OrderRequest.class)))
                .thenThrow(new ProductServiceUnavailableException("Product Service is unavailable", new RuntimeException()));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable());
    }
}
