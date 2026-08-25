package com.order.service.controller;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.client.ProductClient;
import com.order.service.event.OrderCreatedEvent;
import com.order.service.exception.InsufficientStockException;
import com.order.service.exception.ProductNotFoundException;
import com.order.service.model.Order;
import com.order.service.model.ProductResponse;
import com.order.service.repository.OrderRepository;
import com.order.service.repository.OutboxEventRepository;

import feign.FeignException;

@Service
public class OrderService {
	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	private final ProductClient productClient;
	private final OrderRepository orderRepository;
	private final OutboxEventRepository outboxEventRepository;

	public OrderService(OrderRepository orderRepository, ProductClient productClient,
			OutboxEventRepository outboxEventRepository) {

		this.orderRepository = orderRepository;
		this.productClient = productClient;
		this.outboxEventRepository = outboxEventRepository;
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Optional<Order> getOrderById(Long id) {
		return orderRepository.findById(id);
	}

	public boolean deleteOrder(Long id) {

		if (orderRepository.existsById(id)) {
			orderRepository.deleteById(id);
			return true;
		}

		return false;
	}

	@Transactional
	public Order createOrder(Order order) {
		log.info("Creating order for productId={}, quantity={}", order.getProductId(), order.getQuantity());
		try {
			// reserve stock
			productClient.reserveStock(order.getProductId(), order.getQuantity());
			/*
			 * // get product log.info("Calling Product Service: productId={}",
			 * order.getProductId()); ProductResponse product =
			 * productClient.getProduct(order.getProductId());
			 * log.info("Product found: productId={}", order.getProductId()); // get stock
			 * boolean stockAvailable = productClient.checkStock(order.getProductId(),
			 * order.getQuantity()); if (!stockAvailable) {
			 * log.warn("Insufficient stock: productId={}, quantity={}",
			 * order.getProductId(), order.getQuantity()); throw new
			 * InsufficientStockException("Insufficient stock for product: " +
			 * order.getProductId()); } // reduce stock
			 * productClient.updateStock(order.getProductId(), order.getQuantity());
			 */
			log.info("Stock reserved: productId={}, quantity={}", order.getProductId(), order.getQuantity());
			// save order
			Order savedOrder = orderRepository.save(order);

			/*
			 * OrderCreatedEvent event = new OrderCreatedEvent(savedOrder.getId(),
			 * savedOrder.getProductId(), savedOrder.getQuantity());
			 */

			OrderCreatedEvent event = new OrderCreatedEvent(savedOrder.getId(), savedOrder.getProductId(),
					savedOrder.getQuantity());
			ObjectMapper mapper = new ObjectMapper();
			OutboxService outboxService = new OutboxService(outboxEventRepository, mapper);
			outboxService.saveOrderCreatedEvent(event);
			// orderEventProducer.publishOrderCreated(event);

			return savedOrder;

		} catch (FeignException.NotFound ex) {
			throw new ProductNotFoundException("Product not found: " + order.getProductId());
		} catch (FeignException.Conflict ex) {
			throw new InsufficientStockException("Insufficient stock for product: " + order.getProductId());
		} catch (Exception e) {
			log.error("Order processing failed after stock reservation. " + "Restocking productId={}, quantity={}",
					order.getProductId(), order.getQuantity(), e);
			// productClient.releaseStock(order.getProductId(), order.getQuantity());
			throw e;
		}
	}

	public ProductResponse getProduct(Long productId) {
		return productClient.getProduct(productId);
	}
}