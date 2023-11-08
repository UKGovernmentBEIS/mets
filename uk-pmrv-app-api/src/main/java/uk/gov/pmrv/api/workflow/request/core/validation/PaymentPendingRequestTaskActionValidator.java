package uk.gov.pmrv.api.workflow.request.core.validation;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

@Service
public class PaymentPendingRequestTaskActionValidator implements RequestTaskActionValidator {

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {

        final Boolean paymentCompleted = requestTask.getRequest().getPayload().getPaymentCompleted();
        return Boolean.TRUE.equals(paymentCompleted) ?
            RequestTaskActionValidationResult.validResult() :
            RequestTaskActionValidationResult.invalidResult(RequestTaskActionValidationResult.ErrorMessage.PAYMENT_IN_PROGRESS);
    }

    @Override
    public Set<RequestTaskActionType> getTypes() {
        Set<RequestTaskActionType> requestTaskActionTypes = new HashSet<>();
        requestTaskActionTypes.addAll(RequestTaskActionType.getNotifyOperatorForDecisionTypesBlockedByPayment());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRequestPeerReviewTypesBlockedByPayment());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRfiRdeSubmissionTypes());

        return requestTaskActionTypes;
    }
}
