package enterpriseapplication.orderservice.dto;

import java.math.BigDecimal;

/**
 * Subset of the Product Service's ProductResponse needed to price an order.
 */
public record ProductDetails(Long productId, String name, BigDecimal unitPrice) {
}
