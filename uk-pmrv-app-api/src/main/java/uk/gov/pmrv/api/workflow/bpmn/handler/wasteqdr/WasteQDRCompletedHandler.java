package uk.gov.pmrv.api.workflow.bpmn.handler.wasteqdr;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WasteQDRCompletedHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

    }
}
