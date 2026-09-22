package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.service.FeePaymentService;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PaymentVariationManualProperties;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRequestPaymentDetails;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountByRequestTypeService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentDeterminePermitVariationAmountService
        implements PaymentDetermineAmountByRequestTypeService {

    private final PaymentFeeMethodService paymentFeeMethodService;
    private final PaymentVariationManualProperties paymentVariationManualProperties;
    private final List<FeePaymentService> feePaymentServices;
    private final InstallationAccountQueryService installationAccountQueryService;


    @Override
    public BigDecimal determineAmount(Request request) {
        EmitterType emitterType = installationAccountQueryService
                .getInstallationAccountInfoDTOById(request.getAccountId()).getEmitterType();

        Optional<FeeMethodType> feeMethodType =
                paymentFeeMethodService.getFeeMethodType(
                        request.getCompetentAuthority(),
                        request.getType()
                );

        return feeMethodType
                .map(type -> {
                    if (isManualPaymentEnabled(request, emitterType)) {
                        return determineManualPaymentAmount(request);
                    }

                    return getFeeAmountService(type)
                            .map(service -> service.getAmount(request))
                            .orElseThrow(() ->
                                    new BusinessException(
                                            ErrorCode.FEE_CONFIGURATION_NOT_EXIST
                                    ));
                })
                .orElse(BigDecimal.ZERO);
    }

    private boolean isManualPaymentEnabled(Request request, EmitterType emitterType) {
        return paymentVariationManualProperties.getCas()
                .contains(request.getCompetentAuthority())
                && paymentFeeMethodService.isZeroNonChangeableFeeConfigured(
                request.getCompetentAuthority(),
                request.getType(),
                EmitterType.WASTE.equals(emitterType) ? FeeType.WASTE : FeeType.FIXED
        );
    }

    private BigDecimal determineManualPaymentAmount(Request request) {
        PermitVariationRequestPayload requestPayload =
                (PermitVariationRequestPayload) request.getPayload();

        PermitVariationRequestPaymentDetails paymentDetails =
                requestPayload.getRequestPaymentDetails();

        // The manual payment amount should only be consumed once by the Payment workflow.
        // Once consumed, the pending flag is cleared so that a later standard Payment
        // invocation cannot reuse the same amount.
        if (paymentDetails != null
                && Boolean.TRUE.equals(
                paymentDetails.getManualPaymentInitiationPending()
        )) {

            paymentDetails.setManualPaymentInitiationPending(false);
            return paymentDetails.getAmount();
        }

        return BigDecimal.ZERO;
    }

    private Optional<FeePaymentService> getFeeAmountService(FeeMethodType type) {
        return feePaymentServices.stream()
                .filter(service -> service.getFeeMethodType().equals(type))
                .findFirst();
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PERMIT_VARIATION;
    }
}
