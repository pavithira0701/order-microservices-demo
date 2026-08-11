package com.order.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.order.service.model.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleProductNotFound(ProductNotFoundException ex) {

		ApiResponse<Void> response = new ApiResponse<>(ex.getMessage(), HttpStatus.NOT_FOUND.value(), null);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
}