package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PaymentDetermineAmountServiceFacade {

    private final RequestService requestService;
    private final List<PaymentDetermineAmountByRequestTypeService> paymentDetermineAmountByRequestTypeServices;
    private final PaymentDetermineAmountDefaultService paymentDetermineAmountDefaultService;
    
    @Transactional
    public BigDecimal resolveAmountAndPopulateRequestPayload(String requestId) {
		final BigDecimal amount = resolveAmount(requestId);

		Request request = requestService.findRequestById(requestId);
		RequestPayload requestPayload = request.getPayload();
		requestPayload.setPaymentAmount(amount);	
        return amount;
    }
    
    @Transactional
    public BigDecimal resolveAmount(String requestId) {
    	Request request = requestService.findRequestById(requestId);
		
		Optional<PaymentDetermineAmountByRequestTypeService> byRequestTypeService = getPaymentDetermineAmountByRequestTypeService(
				request.getType());
		final PaymentDetermineAmountService determineAmountService = byRequestTypeService.isPresent()
				? byRequestTypeService.get()
				: paymentDetermineAmountDefaultService;
		return determineAmountService.determineAmount(request);
    }
    
    private Optional<PaymentDetermineAmountByRequestTypeService> getPaymentDetermineAmountByRequestTypeService(RequestType requestType) {
        return paymentDetermineAmountByRequestTypeServices.stream()
            .filter(service -> service.getRequestType() == requestType)
            .findAny();
    }
    
}
