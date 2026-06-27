package enterpriseapplication.orderservice.service;

import enterpriseapplication.orderservice.client.ProductServiceClient;
import enterpriseapplication.orderservice.dto.OrderRequest;
import enterpriseapplication.orderservice.dto.OrderResponse;
import enterpriseapplication.orderservice.dto.ProductDetails;
import enterpriseapplication.orderservice.entity.Order;
import enterpriseapplication.orderservice.exception.ProductNotFoundException;
import enterpriseapplication.orderservice.messaging.OrderCreatedEvent;
import enterpriseapplication.orderservice.messaging.OrderEventPublisher;
import enterpriseapplication.orderservice.repository.OrderRepository;
import enterpriseapplication.orderservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, productServiceClient, orderEventPublisher);
    }

    @Test
    void createOrder_fetchesProduct_calculatesTotal_savesOrder_andPublishesEvent() {
        OrderRequest request = new OrderRequest(1L, 10L, 3);
        ProductDetails product = new ProductDetails(10L, "Laptop", new BigDecimal("100.00"));
        when(productServiceClient.getProductById(10L)).thenReturn(product);

        Order saved = new Order(1L, 1L, 10L, "Laptop", 3, new BigDecimal("300.00"), LocalDateTime.now(), "CREATED");
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        OrderResponse response = orderService.createOrder(request);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getTotalPrice()).isEqualTo(new BigDecimal("300.00"));
        assertThat(orderCaptor.getValue().getProductName()).isEqualTo("Laptop");

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(orderEventPublisher).publishOrderCreated(eventCaptor.capture());
        assertThat(eventCaptor.getValue().orderId()).isEqualTo(1L);

        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getTotalPrice()).isEqualTo(new BigDecimal("300.00"));
        assertThat(response.getStatus()).isEqualTo("CREATED");
    }

    @Test
    void createOrder_throws_whenProductNotFound() {
        OrderRequest request = new OrderRequest(1L, 99L, 2);
        when(productServiceClient.getProductById(99L)).thenThrow(new ProductNotFoundException(99L));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
