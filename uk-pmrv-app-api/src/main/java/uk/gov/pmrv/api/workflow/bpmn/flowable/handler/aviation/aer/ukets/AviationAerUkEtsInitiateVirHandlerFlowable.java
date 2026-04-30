package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.ukets;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreateVirService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AviationAerUkEtsInitiateVirHandlerFlowable implements JavaDelegate {

    private final AviationAerCreateVirService service;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
        service.createRequestVir(requestId);
    }
}
