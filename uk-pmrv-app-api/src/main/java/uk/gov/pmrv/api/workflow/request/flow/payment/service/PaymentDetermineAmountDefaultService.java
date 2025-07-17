package uk.gov.pmrv.api.workflow.request.flow.payment.service;

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

@Service
@RequiredArgsConstructor
class PaymentDetermineAmountDefaultService implements PaymentDetermineAmountService {

	private final PaymentFeeMethodService paymentFeeMethodService;
    private final List<FeePaymentService> feePaymentServices;
    
    @Override
	public BigDecimal determineAmount(Request request) {
		final Optional<FeeMethodType> feeMethodType = paymentFeeMethodService
				.getFeeMethodType(request.getCompetentAuthority(), request.getType());
		return feeMethodType
				.map(type -> getFeeAmountService(type).map(service -> service.getAmount(request))
						.orElseThrow(() -> new BusinessException(ErrorCode.FEE_CONFIGURATION_NOT_EXIST)))
				.orElse(BigDecimal.ZERO);
	}

    private Optional<FeePaymentService> getFeeAmountService(FeeMethodType feeMethodType) {
        return feePaymentServices.stream()
                .filter(service -> feeMethodType == service.getFeeMethodType())
                .findAny();
    }

}
