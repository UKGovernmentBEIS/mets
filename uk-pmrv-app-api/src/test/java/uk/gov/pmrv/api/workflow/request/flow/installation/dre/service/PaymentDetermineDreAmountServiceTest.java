package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreFee;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreFeeDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentDetermineDreAmountServiceTest {

    @InjectMocks
    private PaymentDetermineDreAmountService paymentDetermineDreAmountService;

    @Mock
    private PaymentFeeMethodService paymentFeeMethodService;

    @Test
    void determineAmount() {
        String requestId = "1";
        Dre dre = Dre.builder()
				.fee(DreFee.builder()
						.chargeOperator(true)
						.feeDetails(DreFeeDetails.builder()
								.hourlyRate(BigDecimal.TEN)
								.totalBillableHours(BigDecimal.TEN)
								.build())
						.build())
				.build();
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        RequestType requestType = RequestType.DRE;
        Request request = Request.builder()
                .id(requestId)
                .competentAuthority(competentAuthority)
                .type(requestType)
                .payload(DreRequestPayload.builder()
                		.dre(dre).build())
                .build();

        when(paymentFeeMethodService.getFeeMethodType(competentAuthority, requestType)).thenReturn(Optional.of(FeeMethodType.STANDARD));
        BigDecimal actualAmount = paymentDetermineDreAmountService.determineAmount(request);
        assertThat(actualAmount).isEqualTo(dre.getFee().getFeeAmount());
    }

    @Test
    void determineAmount_fee_method_type_not_defined() {
        String requestId = "1";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        RequestType requestType = RequestType.DRE;
        BigDecimal amount = BigDecimal.valueOf(3625.0);
        Request request = Request.builder()
                .id(requestId)
                .competentAuthority(competentAuthority)
                .type(requestType)
                .payload(DreRequestPayload.builder().paymentAmount(amount).build())
                .build();

        when(paymentFeeMethodService.getFeeMethodType(competentAuthority, requestType)).thenReturn(Optional.empty());
        BigDecimal actualAmount = paymentDetermineDreAmountService.determineAmount(request);
        assertEquals(BigDecimal.ZERO, actualAmount);
    }

    @Test
    void getRequestTypes() {
        assertEquals(paymentDetermineDreAmountService.getRequestType(), RequestType.DRE);
    }
}