package com.github.underscorenico.oms.processes.hello.listeners;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Listener {

	@Autowired
	private RuntimeService runtimeService;

	@KafkaListener(topics = "hello.t")
	public void listen(String data) {
		// Retrieve the process instance id (execution id key in the message)
		String processInstanceId = data;
		log.debug("Resume process instance: " + processInstanceId);
		// Signal (resume) execution
		runtimeService.signal(processInstanceId);
	}
}
