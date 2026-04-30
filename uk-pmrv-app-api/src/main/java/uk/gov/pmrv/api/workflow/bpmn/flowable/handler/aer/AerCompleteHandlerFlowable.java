package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aer;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCompleteService;

@Service
@RequiredArgsConstructor
public class AerCompleteHandlerFlowable implements JavaDelegate {

    private final AerCompleteService aerCompleteService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aerCompleteService.complete(requestId);
    }
}
