package com.order.service.controller;

import org.springframework.stereotype.Service;

import com.order.service.client.ProductClient;
import com.order.service.model.Order;
import com.order.service.model.ProductResponse;
import com.order.service.repository.OrderRepository;

@Service
public class OrderService {

	private final ProductClient productClient;
	private final OrderRepository orderRepository;

	public OrderService(OrderRepository orderRepository, ProductClient productClient) {

		this.orderRepository = orderRepository;
		this.productClient = productClient;
	}

	public Order createOrder(Order order) {
		ProductResponse product = productClient.getProduct(order.getProductId());
		if (product == null) {
			return null;
		}
		return orderRepository.save(order);
	}

	public ProductResponse getProduct(Long productId) {
		return productClient.getProduct(productId);
	}
}