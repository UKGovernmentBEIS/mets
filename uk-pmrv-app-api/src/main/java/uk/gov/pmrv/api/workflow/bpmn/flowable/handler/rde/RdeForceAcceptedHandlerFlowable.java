package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.rde;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeDecisionForcedService;

@Service
@RequiredArgsConstructor
public class RdeForceAcceptedHandlerFlowable implements JavaDelegate {

    private final RdeDecisionForcedService service;

    @Override
    public void execute(DelegateExecution execution) {
        service.force((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
