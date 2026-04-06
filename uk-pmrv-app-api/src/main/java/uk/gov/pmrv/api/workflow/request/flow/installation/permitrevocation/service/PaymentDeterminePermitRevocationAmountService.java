package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.FeePaymentService;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountByRequestTypeService;

@Service
@RequiredArgsConstructor
public class PaymentDeterminePermitRevocationAmountService implements PaymentDetermineAmountByRequestTypeService {

    private final PaymentFeeMethodService paymentFeeMethodService;
    private final List<FeePaymentService> feePaymentServices;

    @Override
    public BigDecimal determineAmount(Request request) {
        if (request.getPayload() instanceof PermitRevocationRequestPayload permitRevocationRequestPayload) {
            // Permit revocation "no fee" path: force payment amount to zero.
            PermitRevocation permitRevocation = permitRevocationRequestPayload.getPermitRevocation();
            if (permitRevocation != null && Boolean.FALSE.equals(permitRevocation.getFeeCharged())) {
                return BigDecimal.ZERO;
            }
        }

        final Optional<FeeMethodType> feeMethodType =
            paymentFeeMethodService.getFeeMethodType(request.getCompetentAuthority(), request.getType());

        return feeMethodType
            .map(type -> getFeeAmountService(type)
                .map(service -> service.getAmount(request))
                .orElseThrow(() -> new BusinessException(ErrorCode.FEE_CONFIGURATION_NOT_EXIST)))
            .orElse(BigDecimal.ZERO);
    }

    private Optional<FeePaymentService> getFeeAmountService(FeeMethodType feeMethodType) {
        return feePaymentServices.stream()
            .filter(service -> feeMethodType == service.getFeeMethodType())
            .findAny();
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PERMIT_REVOCATION;
    }
}

