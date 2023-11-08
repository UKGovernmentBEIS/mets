package uk.gov.pmrv.api.workflow.bpmn.handler.payment;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountServiceFacade;

@Component
@RequiredArgsConstructor
public class DeterminePaymentAmountHandler implements JavaDelegate {

    private final PaymentDetermineAmountServiceFacade paymentDetermineAmountServiceFacade;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        BigDecimal paymentAmount = paymentDetermineAmountServiceFacade.resolveAmountAndPopulateRequestPayload(requestId);

        execution.setVariable(BpmnProcessConstants.PAYMENT_AMOUNT, paymentAmount);
    }
}
