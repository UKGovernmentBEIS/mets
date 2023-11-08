package uk.gov.pmrv.api.workflow.bpmn.handler.ner;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplicationAddDeemedWithdrawnRequestActionService;

@Service
@RequiredArgsConstructor
public class NerApplicationAddDeemedWithdrawnRequestActionHandler implements JavaDelegate {

    private final NerApplicationAddDeemedWithdrawnRequestActionService service;

    @Override
    public void execute(final DelegateExecution execution) {
        service.deemWithdrawn((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
