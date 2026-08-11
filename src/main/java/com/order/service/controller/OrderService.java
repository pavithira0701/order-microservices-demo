package com.order.service.controller;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.order.service.client.ProductClient;
import com.order.service.exception.InsufficientStockException;
import com.order.service.exception.ProductNotFoundException;
import com.order.service.model.Order;
import com.order.service.model.ProductResponse;
import com.order.service.repository.OrderRepository;

import feign.FeignException;

@Service
public class OrderService {
	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	private final ProductClient productClient;
	private final OrderRepository orderRepository;

	public OrderService(OrderRepository orderRepository, ProductClient productClient) {

		this.orderRepository = orderRepository;
		this.productClient = productClient;
	}

	public Order createOrder(Order order) {
		log.info("Creating order for productId={}, quantity={}", order.getProductId(), order.getQuantity());
		try {
			log.info("Calling Product Service: productId={}", order.getProductId());
			ProductResponse product = productClient.getProduct(order.getProductId());
			log.info("Product found: productId={}", order.getProductId());
			boolean stockAvailable = productClient.checkStock(order.getProductId(), order.getQuantity());
			if (!stockAvailable) {
				log.warn("Insufficient stock: productId={}, quantity={}", order.getProductId(), order.getQuantity());
				throw new InsufficientStockException("Insufficient stock for product: " + order.getProductId());
			}
			return orderRepository.save(order);
		} catch (FeignException.NotFound ex) {
			log.warn("Product not found: productId={}", order.getProductId());
			throw new ProductNotFoundException("Product not found: " + order.getProductId());
		}
	}

	public ProductResponse getProduct(Long productId) {
		return productClient.getProduct(productId);
	}
}