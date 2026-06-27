package enterpriseapplication.orderservice.service;

import enterpriseapplication.orderservice.dto.OrderRequest;
import enterpriseapplication.orderservice.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);
}
