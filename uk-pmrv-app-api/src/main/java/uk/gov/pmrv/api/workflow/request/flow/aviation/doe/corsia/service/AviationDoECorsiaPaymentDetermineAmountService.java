package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountByRequestTypeService;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationDoECorsiaPaymentDetermineAmountService implements PaymentDetermineAmountByRequestTypeService {

    private final PaymentFeeMethodService paymentFeeMethodService;

    @Override
    public BigDecimal determineAmount(Request request) {
        final Optional<FeeMethodType> feeMethodType = paymentFeeMethodService.getFeeMethodType(request.getCompetentAuthority(), request.getType());
        return feeMethodType.map(type -> {
                    AviationDoECorsiaRequestPayload requestPayload = (AviationDoECorsiaRequestPayload) request.getPayload();
                    return requestPayload.getDoe().getFee().getFeeAmount();
                })
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.AVIATION_DOE_CORSIA;
    }
}
