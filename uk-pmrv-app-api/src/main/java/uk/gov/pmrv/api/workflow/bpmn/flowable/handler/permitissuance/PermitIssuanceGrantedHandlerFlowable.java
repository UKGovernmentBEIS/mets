package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitissuance;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceGrantedService;

@Service
@RequiredArgsConstructor
public class PermitIssuanceGrantedHandlerFlowable implements JavaDelegate {

    private final PermitIssuanceGrantedService service;

    @Override
    public void execute(DelegateExecution execution) {
        
        service.grant((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
