package com.github.underscorenico.oms.processes.hello.delegates;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PingDelegate implements JavaDelegate {

    public void execute(DelegateExecution delegateExecution) throws Exception {

        log.debug("Pingin'!");
    }
}
