package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PaymentVariationManualProperties;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentStatus;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermitVariationRequestService {

    private static final Set<RequestTaskType> PAYMENT_TASK_TYPES = Set.of(
            RequestTaskType.PERMIT_VARIATION_MAKE_PAYMENT,
            RequestTaskType.PERMIT_VARIATION_TRACK_PAYMENT,
            RequestTaskType.PERMIT_VARIATION_CONFIRM_PAYMENT
    );

    private final RequestRepository requestRepository;
    private final PaymentVariationManualProperties paymentVariationManualProperties;
    private final PaymentFeeMethodService paymentFeeMethodService;
    private final RequestTaskRepository requestTaskRepository;
    private final WorkflowService workflowService;
    private final InstallationAccountQueryService installationAccountQueryService;

    @Transactional(readOnly = true)
    public boolean canRequestPayment(String requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!RequestStatus.IN_PROGRESS.equals(request.getStatus())) {
            return false;
        }

        EmitterType emitterType = installationAccountQueryService
                .getInstallationAccountInfoDTOById(request.getAccountId()).getEmitterType();

        if (!isManualPaymentEnabled(request, emitterType)) {
            return false;
        }

        return canInitiatePayment(
                requestId,
                (PermitVariationRequestPayload) request.getPayload()
        );
    }

    private boolean isManualPaymentEnabled(Request request,EmitterType emitterType) {
        return paymentVariationManualProperties.getCas()
                .contains(request.getCompetentAuthority())
                && paymentFeeMethodService.isZeroNonChangeableFeeConfigured(
                request.getCompetentAuthority(),
                RequestType.PERMIT_VARIATION,
                EmitterType.WASTE.equals(emitterType) ? FeeType.WASTE : FeeType.FIXED

        );
    }

    private boolean canInitiatePayment(
            String requestId,
            PermitVariationRequestPayload payload) {

        boolean paymentTaskExists = requestTaskRepository.findByRequestId(requestId)
                .stream()
                .map(RequestTask::getType)
                .anyMatch(PAYMENT_TASK_TYPES::contains);

        if (paymentTaskExists) {
            return false;
        }

        RequestPaymentInfo paymentInfo = payload.getRequestPaymentInfo();

        boolean paymentStatusAllowsInitiation =
                paymentInfo == null
                        || PaymentStatus.CANCELLED.equals(paymentInfo.getStatus());

        return paymentStatusAllowsInitiation
                && workflowService.hasMessageEventSubscriptionWithName(
                requestId,
                BpmnProcessConstants.PERMIT_VARIATION_INITIATE_PAYMENT
        );
    }
}

