package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewRejectedService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderRejectedHandler implements JavaDelegate {
    
    private final PermitSurrenderReviewRejectedService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        service.executeRejectedPostActions((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
