package com.order.service.controller;

import java.util.List;
import jakarta.validation.Valid;
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

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Create order
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @Valid @RequestBody Order order) {

        Order createdOrder = orderService.createOrder(order);

        ApiResponse<Order> response =
                new ApiResponse<>(
                        "Order placed Successfully",
                        HttpStatus.CREATED.value(),
                        createdOrder);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id) {

        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }

    // Delete / cancel order
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @PathVariable Long id) {

        boolean deleted = orderService.deleteOrder(id);

        if (deleted) {

            ApiResponse<Void> response =
                    new ApiResponse<>(
                            "Order cancelled successfully with ID: " + id,
                            HttpStatus.OK.value());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }
}
