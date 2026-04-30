package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitsurrender;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewDeemedWithdrawnService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderDeemedWithdrawnHandlerFlowable implements JavaDelegate {
    
    private final PermitSurrenderReviewDeemedWithdrawnService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        service.executeDeemedWithdrawnPostActions((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
