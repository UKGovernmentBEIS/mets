package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRequestPaymentActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRequestPaymentDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationRequestService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class PermitVariationRequestPaymentActionHandler {

    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final PermitVariationRequestService permitVariationRequestService;

    @Transactional
    public void process(final String requestId,
                        final AppUser appUser,
                        final PermitVariationRequestPaymentDetails paymentDetails) {

        final Request request = requestService.findRequestById(requestId);

        if (!permitVariationRequestService.canRequestPayment(requestId)) {
            throw new BusinessException(ErrorCode.REQUEST_TASK_ACTION_CANNOT_PROCEED);
        }

        PermitVariationRequestPaymentActionPayload requestActionPayload =
                PermitVariationRequestPaymentActionPayload.builder()
                        .paymentDetails(paymentDetails)
                        .payloadType(RequestActionPayloadType.PERMIT_VARIATION_REQUEST_PAYMENT_PAYLOAD)
                        .build();

        paymentDetails.setManualPaymentInitiationPending(true);
        PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
        requestPayload.setRequestPaymentDetails(paymentDetails);

        requestService.addActionToRequest(
                request,
                requestActionPayload,
                RequestActionType.PERMIT_VARIATION_REQUEST_PAYMENT,
                appUser.getUserId()
        );

        LocalDate paymentDueDate = requestPayload.getRequestPaymentDetails().getDueDate();
        final Date paymentExpirationDate = DateUtils.atEndOfDay(paymentDueDate);

        workflowService.sendEvent(
                requestId,
                BpmnProcessConstants.PERMIT_VARIATION_INITIATE_PAYMENT,
                Map.of(BpmnProcessConstants.PAYMENT_EXPIRES, true,
                        BpmnProcessConstants.PAYMENT_EXPIRATION_DATE, paymentExpirationDate)
        );

    }
}
