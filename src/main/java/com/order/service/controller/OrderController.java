package com.order.service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.service.model.ApiResponse;
import com.order.service.model.Order;
import com.order.service.repository.OrderRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private OrderRepository orderRepository = null;

	private OrderService orderService = null;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	/*
	 * public OrderController(OrderRepository orderRepository) {
	 * this.orderRepository = orderRepository; }
	 */

	// to create new order
	@PostMapping
	public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody Order order) {
		Order createdOrder = orderService.createOrder(order);
		if (createdOrder == null) {
			return ResponseEntity.notFound().build();
		}
		ApiResponse<Order> response = new ApiResponse<>("Order placed Successfully", HttpStatus.CREATED.value(),
				createdOrder);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// get all orders
	@GetMapping
	public ResponseEntity<List<Order>> getAllProducts() {
		return ResponseEntity.ok(orderRepository.findAll());
	}

	// get order by id
	@GetMapping("/{id}")
	public ResponseEntity<Order> getProductById(@PathVariable Long id) {
		Optional<Order> product = orderRepository.findById(id);
		return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// cancel order by id
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
		if (orderRepository.existsById(id)) {
			orderRepository.deleteById(id);
			ApiResponse<Void> response = new ApiResponse<>("Order cancelled successfully with ID: " + id,
					HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.notFound().build();
	}
}
