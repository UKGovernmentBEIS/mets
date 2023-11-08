package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountByRequestTypeService;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentDetermineAviationDreUkEtsAmountService implements PaymentDetermineAmountByRequestTypeService {
    private final PaymentFeeMethodService paymentFeeMethodService;

    @Override
    public BigDecimal determineAmount(Request request) {
        final Optional<FeeMethodType> feeMethodType = paymentFeeMethodService.getFeeMethodType(request.getCompetentAuthority(), request.getType());
        return feeMethodType.map(type -> {
                    AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) request.getPayload();
                    return requestPayload.getDre().getFee().getFeeAmount();
                })
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.AVIATION_DRE_UKETS;
    }
}
