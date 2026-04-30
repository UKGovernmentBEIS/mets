package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aer;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreateVirService;

@Service
@RequiredArgsConstructor
public class AerInitiateVirHandlerFlowable implements JavaDelegate {

    private final AerCreateVirService aerCreateVirService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        aerCreateVirService.createRequestVir(requestId);
    }
}
