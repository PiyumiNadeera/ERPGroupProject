package enterpriseapplication.orderservice.service.impl;

import enterpriseapplication.orderservice.client.ProductServiceClient;
import enterpriseapplication.orderservice.dto.OrderRequest;
import enterpriseapplication.orderservice.dto.OrderResponse;
import enterpriseapplication.orderservice.dto.ProductDetails;
import enterpriseapplication.orderservice.entity.Order;
import enterpriseapplication.orderservice.messaging.OrderCreatedEvent;
import enterpriseapplication.orderservice.messaging.OrderEventPublisher;
import enterpriseapplication.orderservice.repository.OrderRepository;
import enterpriseapplication.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final String STATUS_CREATED = "CREATED";

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final OrderEventPublisher orderEventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository,
                             ProductServiceClient productServiceClient,
                             OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        ProductDetails product = productServiceClient.getProductById(request.getProductId());

        BigDecimal totalPrice = product.unitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(product.productId());
        order.setProductName(product.name());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(STATUS_CREATED);

        Order saved = orderRepository.save(order);

        orderEventPublisher.publishOrderCreated(new OrderCreatedEvent(
                saved.getOrderId(), saved.getCustomerId(), saved.getProductId(), saved.getProductName(),
                saved.getQuantity(), saved.getTotalPrice(), saved.getOrderDate()));

        return toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getOrderDate(),
                order.getStatus());
    }
}
