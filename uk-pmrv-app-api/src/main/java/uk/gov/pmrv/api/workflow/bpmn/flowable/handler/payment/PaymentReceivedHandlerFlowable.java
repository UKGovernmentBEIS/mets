package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.payment;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentCompleteRequestActionService;

@Component
@RequiredArgsConstructor
public class PaymentReceivedHandlerFlowable implements JavaDelegate {

    private final PaymentCompleteRequestActionService paymentCompleteRequestActionService;

    @Override
    public void execute(DelegateExecution execution) {
        paymentCompleteRequestActionService.markAsReceived((String) execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
