package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountServiceFacade;

@ExtendWith(MockitoExtension.class)
class PermitRevocationApplicationSubmitInitializerTest {

    @InjectMocks
    private PermitRevocationApplicationSubmitInitializer initializer;

    @Mock
    private PaymentDetermineAmountServiceFacade paymentDetermineAmountServiceFacade;

    @Test
    void initializePayload() {
        final BigDecimal feeAmount = BigDecimal.valueOf(2450);
        
        final Request request = Request.builder()
        		.id("id")
        		.payload(PermitRevocationRequestPayload.builder()
        				.paymentAmount(feeAmount)
        				.build())
        		.build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitRevocationApplicationSubmitRequestTaskPayload.class);

        PermitRevocationApplicationSubmitRequestTaskPayload permitRevocationApplicationSubmitRequestTaskPayload =
            (PermitRevocationApplicationSubmitRequestTaskPayload)requestTaskPayload;
        assertThat(permitRevocationApplicationSubmitRequestTaskPayload.getFeeAmount()).isEqualTo(feeAmount);
        
        verify(paymentDetermineAmountServiceFacade, times(1)).resolveAmountAndPopulateRequestPayload("id");
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.PERMIT_REVOCATION_APPLICATION_SUBMIT));
    }
}
