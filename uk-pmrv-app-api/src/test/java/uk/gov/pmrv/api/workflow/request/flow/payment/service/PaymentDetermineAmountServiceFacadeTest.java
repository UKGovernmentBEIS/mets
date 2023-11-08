package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.PaymentDetermineDreAmountService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class PaymentDetermineAmountServiceFacadeTest {

    private PaymentDetermineAmountServiceFacade cut;

    private PaymentDetermineAmountDefaultService paymentDetermineAmountDefaultService;
    private PaymentDetermineDreAmountService paymentDetermineDreAmountService;
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        paymentDetermineDreAmountService = mock(PaymentDetermineDreAmountService.class);
        requestService = mock(RequestService.class);

        paymentDetermineAmountDefaultService = mock(PaymentDetermineAmountDefaultService.class);
        cut = new PaymentDetermineAmountServiceFacade(
            requestService,
            List.of(paymentDetermineDreAmountService),
            paymentDetermineAmountDefaultService
            );
    }

    @Test
    void resolveAmountAndPopulateRequestPayload() {
        String requestId = "AEM-123-4";
        PermitIssuanceRequestPayload payload = PermitIssuanceRequestPayload.builder().build();
        Request request = Request.builder().id(requestId).type(RequestType.PERMIT_ISSUANCE)
        		.payload(payload)
        		.build();
        BigDecimal amount = BigDecimal.valueOf(500);

        when(paymentDetermineDreAmountService.getRequestType()).thenReturn(RequestType.DRE);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(paymentDetermineAmountDefaultService.determineAmount(request)).thenReturn(amount);

        BigDecimal result = cut.resolveAmountAndPopulateRequestPayload(requestId);
        
        assertThat(result).isEqualTo(amount);
        assertThat(payload.getPaymentAmount()).isEqualTo(amount);

        verify(paymentDetermineDreAmountService, times(1)).getRequestType();
        verifyNoMoreInteractions(paymentDetermineDreAmountService);
        verify(paymentDetermineAmountDefaultService, times(1)).determineAmount(request);
    }
    
    @Test
    void resolveAmount() {
        String requestId = "AEM-123-4";
        PermitIssuanceRequestPayload payload = PermitIssuanceRequestPayload.builder().build();
        Request request = Request.builder().id(requestId).type(RequestType.PERMIT_ISSUANCE)
        		.payload(payload)
        		.build();
        BigDecimal amount = BigDecimal.valueOf(500);

        when(paymentDetermineDreAmountService.getRequestType()).thenReturn(RequestType.DRE);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(paymentDetermineAmountDefaultService.determineAmount(request)).thenReturn(amount);

        BigDecimal result = cut.resolveAmount(requestId);
        
        assertThat(result).isEqualTo(amount);

        verify(paymentDetermineDreAmountService, times(1)).getRequestType();
        verifyNoMoreInteractions(paymentDetermineDreAmountService);
        verify(paymentDetermineAmountDefaultService, times(1)).determineAmount(request);
    }

    @Test
    void determineAmount_byRequestType() {
        String requestId = "AEM-123-4";
        DreRequestPayload payload = DreRequestPayload.builder().build();
        Request request = Request.builder().id(requestId).type(RequestType.DRE)
        		.payload(payload)
        		.build();
        BigDecimal amount = BigDecimal.valueOf(500);

        when(paymentDetermineDreAmountService.getRequestType()).thenReturn(RequestType.DRE);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        when(paymentDetermineDreAmountService.determineAmount(request)).thenReturn(amount);

        BigDecimal result = cut.resolveAmountAndPopulateRequestPayload(requestId);
        
        assertThat(result).isEqualTo(amount);
        assertThat(payload.getPaymentAmount()).isEqualTo(amount);

        verify(paymentDetermineDreAmountService, times(1)).getRequestType();
        verify(paymentDetermineDreAmountService, times(1)).determineAmount(request);
        verifyNoInteractions(paymentDetermineAmountDefaultService);
    }
}