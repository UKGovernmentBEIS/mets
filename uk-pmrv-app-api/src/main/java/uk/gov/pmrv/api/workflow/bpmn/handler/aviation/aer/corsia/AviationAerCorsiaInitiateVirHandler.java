package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.corsia;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreateVirService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaInitiateVirHandler implements JavaDelegate {
    
    private final AviationAerCreateVirService service;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.createRequestVir(requestId);
    }
}
