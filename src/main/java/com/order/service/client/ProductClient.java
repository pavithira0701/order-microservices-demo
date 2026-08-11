package com.order.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.order.service.model.ProductResponse;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

	@GetMapping("/api/products/{id}")
	ProductResponse getProduct(@PathVariable Long id);

	/*
	 * @GetMapping("/api/products/{id}/stock") boolean checkStock(@PathVariable Long
	 * id, @RequestParam int quantity);
	 * 
	 * @PutMapping("/api/products/{id}/stock") ProductResponse
	 * updateStock(@PathVariable Long id, @RequestParam int quantity);
	 */

	@PutMapping("/api/products/{id}/stock")
	ProductResponse reserveStock(@PathVariable Long id, @RequestParam int quantity);
}
