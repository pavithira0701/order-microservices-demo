package com.order.service.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.event.OrderCreatedEvent;
import com.order.service.outbox.OutboxEvent;
import com.order.service.repository.OutboxEventRepository;

@Service
public class OutboxService {

	private final OutboxEventRepository repository;
	private final ObjectMapper objectMapper;

	public OutboxService(OutboxEventRepository repository, ObjectMapper objectMapper) {

		this.repository = repository;
		this.objectMapper = objectMapper;
	}

	public void saveOrderCreatedEvent(OrderCreatedEvent event) {

		try {

			String payload = objectMapper.writeValueAsString(event);

			OutboxEvent outboxEvent = new OutboxEvent();

			outboxEvent.setEventType("OrderCreated");
			outboxEvent.setPayload(payload);
			outboxEvent.setStatus("NEW");
			outboxEvent.setCreatedAt(LocalDateTime.now());

			repository.save(outboxEvent);

		} catch (JsonProcessingException e) {

			throw new RuntimeException("Failed to create outbox event", e);
		}
	}
}
