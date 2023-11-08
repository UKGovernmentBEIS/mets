package uk.gov.pmrv.api.workflow.bpmn.handler.ner;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplicationAddRejectedRequestActionService;

@Service
@RequiredArgsConstructor
public class NerApplicationAddRejectedRequestActionHandler implements JavaDelegate {

    private final NerApplicationAddRejectedRequestActionService service;

    @Override
    public void execute(final DelegateExecution execution) {
        service.reject((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
