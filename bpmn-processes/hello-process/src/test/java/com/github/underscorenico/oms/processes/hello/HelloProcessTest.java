package com.github.underscorenico.oms.processes.hello;

import com.github.underscorenico.oms.commons.configurations.KafkaConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {KafkaConsumerConfig.class, TestConfiguration.class})
@Deployment(resources = "processes/hello.bpmn")
public class HelloProcessTest {

	private static final String TEST_TOPIC = "hello.t";

	private KafkaMessageListenerContainer<String, String> container;
	private KafkaTemplate<String, String> template;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, TEST_TOPIC);

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	@Rule
	public ProcessEngineRule processEngineRule;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		String kafkaBootstrapServers = embeddedKafka.getBrokersAsString();
		// override the property in application.properties
		System.setProperty("kafka.servers.bootstrap", kafkaBootstrapServers);
	}

	@Before
	public void setup() throws Exception {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testT", "false", embeddedKafka);
		DefaultKafkaConsumerFactory<String, String> cf =
			new DefaultKafkaConsumerFactory<>(consumerProps);
		ContainerProperties containerProperties = new ContainerProperties(TEST_TOPIC);
		container = new KafkaMessageListenerContainer<>(cf, containerProperties);
		final BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
		container.setupMessageListener((MessageListener<String, String>) record -> {
            log.error("Message received: " + record);
            records.add(record);
        });
		container.start();
		ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
		Map<String, Object> senderProps = KafkaTestUtils.senderProps(embeddedKafka.getBrokersAsString());
		ProducerFactory<String, String> pf =
			new DefaultKafkaProducerFactory<>(senderProps);
		template = new KafkaTemplate<>(pf);
		template.setDefaultTopic(TEST_TOPIC);
	}

	@After
	public void cleanup() {
		container.stop();
	}

	@Test
	public void helloTestOk() throws Exception {

		String orderId = "order1234";
		Map<String, Object> variables = new HashMap<>();
		variables.put("orderId", orderId);
		final String businessKey = "hello_ORDER_ID_1";
		// Manually launch a workflow
		final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("hello", businessKey, variables);

		// Assert that there is one process running
		Assert.assertEquals(1, runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).count());

		ProcessInstance messageConsumerProcess = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
		Assert.assertNotNull(messageConsumerProcess);
		Assert.assertEquals(((ExecutionEntity) messageConsumerProcess).getActivityId(), "msgWait");
		Assert.assertEquals(1, runtimeService.createExecutionQuery().processInstanceId(messageConsumerProcess.getProcessInstanceId()).count());

		// Signal the receive task (simulate the reception of a message)
		template.sendDefault(processInstance.getId());

		// Sleep until the message is consumed and the workflow is correctly resumed
		Thread.sleep(200);

		// Assert the end of the process
		Assert.assertEquals(0, runtimeService.createExecutionQuery().processInstanceId(messageConsumerProcess.getProcessInstanceId()).count());
		Assert.assertEquals(0, runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).count());
	}
}
