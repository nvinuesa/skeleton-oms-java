package com.github.underscorenico.oms.processes.hello;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.h2.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
@ComponentScan
public class TestConfiguration {

	@Bean
	public SimpleDriverDataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(Driver.class);
		dataSource.setUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=-1");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	@Bean
	public DataSourceTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());
		return transactionManager;
	}

	@Bean
	public SpringProcessEngineConfiguration processEngineConfiguration() {
		SpringProcessEngineConfiguration processEngineConfiguration = new SpringProcessEngineConfiguration();
		processEngineConfiguration.setDataSource(dataSource());
		processEngineConfiguration.setTransactionManager(transactionManager());
		processEngineConfiguration.setDatabaseSchemaUpdate("true");
		processEngineConfiguration.setJobExecutorActivate(false);
		// turn off metrics reporter
		processEngineConfiguration.setDbMetricsReporterActivate(false);
		return processEngineConfiguration;
	}

	@Bean
	public ProcessEngineFactoryBean processEngine() throws Exception {
		ProcessEngineFactoryBean engineFactoryBean = new ProcessEngineFactoryBean();
		engineFactoryBean.setProcessEngineConfiguration(processEngineConfiguration());
		return engineFactoryBean;
	}

	@Bean
	public RepositoryService repositoryService() throws Exception {
		return processEngine().getObject().getRepositoryService();
	}

	@Bean
	public RuntimeService runtimeService() throws Exception {
		return processEngine().getObject().getRuntimeService();
	}

	@Bean
	public TaskService taskService() throws Exception {
		return processEngine().getObject().getTaskService();
	}

	@Bean
	public HistoryService historyService() throws Exception {
		return processEngine().getObject().getHistoryService();
	}

	@Bean
	public ManagementService managementService() throws Exception {
		return processEngine().getObject().getManagementService();
	}

	@Bean
	public ProcessEngineRule processEngineRule() throws Exception {
		ProcessEngineRule processEngineRule = new ProcessEngineRule();
		processEngineRule.setProcessEngine(processEngine().getObject());
		return processEngineRule;
	}
}
