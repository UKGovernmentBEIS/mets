package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitsurrender;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderCancelledService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderCancelledHandlerFlowable implements JavaDelegate {

    private final PermitSurrenderCancelledService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        
        service.cancel((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
