package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountByRequestTypeService;

@Service
@RequiredArgsConstructor
public class PaymentDetermineDreAmountService implements PaymentDetermineAmountByRequestTypeService {
	
    private final PaymentFeeMethodService paymentFeeMethodService;

    @Override
    public BigDecimal determineAmount(Request request) {
        final Optional<FeeMethodType> feeMethodType = paymentFeeMethodService.getFeeMethodType(request.getCompetentAuthority(), request.getType());
        return feeMethodType.map(type -> {
                    DreRequestPayload requestPayload = (DreRequestPayload) request.getPayload();
                    return requestPayload.getDre().getFee().getFeeAmount();
                })
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.DRE;
    }
    
}
