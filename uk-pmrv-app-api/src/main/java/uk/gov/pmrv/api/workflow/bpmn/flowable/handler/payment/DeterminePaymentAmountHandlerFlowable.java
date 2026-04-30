package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.payment;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountServiceFacade;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DeterminePaymentAmountHandlerFlowable implements JavaDelegate {

    private final PaymentDetermineAmountServiceFacade paymentDetermineAmountServiceFacade;

    @Override
    public void execute(DelegateExecution execution) {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        BigDecimal paymentAmount = paymentDetermineAmountServiceFacade.resolveAmountAndPopulateRequestPayload(requestId);

        execution.setVariable(BpmnProcessConstants.PAYMENT_AMOUNT, paymentAmount);
    }
}
