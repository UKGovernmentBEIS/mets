package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.rde;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeRespondedService;

@Service
@RequiredArgsConstructor
public class RdeAcceptedHandlerFlowable implements JavaDelegate {

    private final RdeRespondedService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.respond((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
