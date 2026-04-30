package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.rfi;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiOutcome;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiExpiredService;

@Service
@RequiredArgsConstructor
public class RfiExpiredHandlerFlowable implements JavaDelegate {

    private final RfiExpiredService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(BpmnProcessConstants.RFI_OUTCOME, RfiOutcome.EXPIRED);
        service.expire((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
