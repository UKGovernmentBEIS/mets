package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitsurrender;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewRejectedService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderRejectedHandlerFlowable implements JavaDelegate {
    
    private final PermitSurrenderReviewRejectedService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        service.executeRejectedPostActions((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
