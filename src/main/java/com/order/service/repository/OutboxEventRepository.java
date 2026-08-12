package com.order.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.order.service.outbox.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
	List<OutboxEvent> findByStatus(String status);
}