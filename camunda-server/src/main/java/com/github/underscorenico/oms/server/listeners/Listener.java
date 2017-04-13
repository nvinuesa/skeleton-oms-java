package com.github.underscorenico.oms.server.listeners;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Listener {

	@Autowired
	private RuntimeService runtimeService;

	@KafkaListener(id = "foo", topics = "topic1", group = "group1")
	public void listen(ConsumerRecord<?, ?> record) {
		log.debug("Checkout launched externally with message: " + record.toString());
		// The order id would be deserialized from the received message
		String orderId = "Fake order id";
		Map<String, Object> variables = new HashMap<>();
		variables.put("orderId", orderId);
		runtimeService.startProcessInstanceByKey("hello", variables);
	}
}
