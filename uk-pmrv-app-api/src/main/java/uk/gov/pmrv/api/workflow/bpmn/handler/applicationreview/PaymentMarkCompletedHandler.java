package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentMarkCompletedService;

@Service
@RequiredArgsConstructor
public class PaymentMarkCompletedHandler implements JavaDelegate {
    
    private final PaymentMarkCompletedService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        service.paymentCompleted((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}