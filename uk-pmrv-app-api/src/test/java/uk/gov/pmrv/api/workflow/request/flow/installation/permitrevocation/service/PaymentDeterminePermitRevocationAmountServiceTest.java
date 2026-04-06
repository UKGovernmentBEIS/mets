package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.FeePaymentService;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentDetermineAmountByRequestTypeService;

@ExtendWith(MockitoExtension.class)
class PaymentDeterminePermitRevocationAmountServiceTest {

    private PaymentDetermineAmountByRequestTypeService cut;

    @Mock
    private PaymentFeeMethodService paymentFeeMethodService;

    @Mock
    private FeePaymentService feePaymentService;

    @BeforeEach
    void setUp() {
        cut = new PaymentDeterminePermitRevocationAmountService(paymentFeeMethodService, List.of(feePaymentService));
    }

    @Test
    void determineAmount_feeCharged_false_returns_zero_without_fee_lookup() {
        PermitRevocation permitRevocation = PermitRevocation.builder()
            .feeCharged(false)
            .build();

        PermitRevocationRequestPayload payload = PermitRevocationRequestPayload.builder()
            .permitRevocation(permitRevocation)
            .build();

        Request request = Request.builder()
            .id("1")
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .type(RequestType.PERMIT_REVOCATION)
            .payload(payload)
            .build();

        BigDecimal actualAmount = cut.determineAmount(request);

        assertEquals(BigDecimal.ZERO, actualAmount);
        verify(paymentFeeMethodService, never()).getFeeMethodType(request.getCompetentAuthority(), request.getType());
    }

    @Test
    void determineAmount_feeCharged_true_delegates_to_fee_payment_services() {
        PermitRevocation permitRevocation = PermitRevocation.builder()
            .feeCharged(true)
            .build();

        PermitRevocationRequestPayload payload = PermitRevocationRequestPayload.builder()
            .permitRevocation(permitRevocation)
            .build();

        Request request = Request.builder()
            .id("1")
            .competentAuthority(CompetentAuthorityEnum.WALES)
            .type(RequestType.PERMIT_REVOCATION)
            .payload(payload)
            .build();

        BigDecimal expectedAmount = BigDecimal.valueOf(123.45);

        when(paymentFeeMethodService.getFeeMethodType(request.getCompetentAuthority(), request.getType()))
            .thenReturn(Optional.of(FeeMethodType.STANDARD));
        when(feePaymentService.getFeeMethodType()).thenReturn(FeeMethodType.STANDARD);
        when(feePaymentService.getAmount(request)).thenReturn(expectedAmount);

        BigDecimal actualAmount = cut.determineAmount(request);

        assertEquals(expectedAmount, actualAmount);
        verify(paymentFeeMethodService).getFeeMethodType(request.getCompetentAuthority(), request.getType());
    }
}

