package enterpriseapplication.orderservice.messaging;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCreatedEvent(Long orderId, Long customerId, Long productId, String productName,
                                 Integer quantity, BigDecimal totalPrice, LocalDateTime orderDate) implements Serializable {
}
