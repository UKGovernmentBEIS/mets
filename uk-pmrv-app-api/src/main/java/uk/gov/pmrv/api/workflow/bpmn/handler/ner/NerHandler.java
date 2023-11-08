package uk.gov.pmrv.api.workflow.bpmn.handler.ner;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class NerHandler implements JavaDelegate {

    @Override
    public void execute(final DelegateExecution execution) {
        // placeholder handler
    }
}
