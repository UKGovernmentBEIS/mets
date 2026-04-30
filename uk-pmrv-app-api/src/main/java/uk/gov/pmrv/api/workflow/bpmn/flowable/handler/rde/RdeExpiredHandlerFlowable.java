package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.rde;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeOutcome;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeExpiredService;

@Service
@RequiredArgsConstructor
public class RdeExpiredHandlerFlowable implements JavaDelegate {

    private final RdeExpiredService service;

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(BpmnProcessConstants.RDE_OUTCOME, RdeOutcome.EXPIRED);
        service.expire((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
