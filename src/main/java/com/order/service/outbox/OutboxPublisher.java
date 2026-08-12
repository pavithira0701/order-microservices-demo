package com.order.service.outbox;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import com.order.service.repository.OutboxEventRepository;

@Component
public class OutboxPublisher {
	private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
	private static final String TOPIC = "order-created";
	private final OutboxEventRepository repository;
	private final KafkaTemplate<String, String> kafkaTemplate;

	public OutboxPublisher(OutboxEventRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
		this.repository = repository;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Scheduled(fixedDelay = 5000)
	public void publishEvents() {
		List<OutboxEvent> events = repository.findByStatus("NEW");
		for (OutboxEvent event : events) {
			try {
				kafkaTemplate.send(TOPIC, event.getId().toString(), event.getPayload()).whenComplete((result, ex) -> {
					if (ex == null) {
						event.setStatus("PUBLISHED");
						repository.save(event);
						log.info("Event {} published", event.getId());
					} else {
						log.error("Failed to publish event {}. " + "It will remain NEW.", event.getId(), ex);
					}
				});

			} catch (Exception ex) {
				System.out.println("Failed to publish event: " + event.getId());
			}
		}
	}
}