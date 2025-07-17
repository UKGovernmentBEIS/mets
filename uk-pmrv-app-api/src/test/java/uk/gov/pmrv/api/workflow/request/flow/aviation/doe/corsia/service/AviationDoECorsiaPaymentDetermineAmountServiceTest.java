package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaFee;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaFeeDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaPaymentDetermineAmountServiceTest {

    @InjectMocks
    private AviationDoECorsiaPaymentDetermineAmountService paymentDetermineDreAmountService;

    @Mock
    private PaymentFeeMethodService paymentFeeMethodService;

     @Test
    void getAmount() {
        String requestId = "1";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        RequestType requestType = RequestType.AVIATION_DOE_CORSIA;
        BigDecimal amount = BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP);
        Request request = Request.builder()
                .id(requestId)
                .competentAuthority(competentAuthority)
                .type(requestType)
                .payload(AviationDoECorsiaRequestPayload.builder()
                        .doe(AviationDoECorsia.builder().fee(AviationDoECorsiaFee.builder()
                                        .chargeOperator(true)
                                        .feeDetails(AviationDoECorsiaFeeDetails.builder()
                                                .hourlyRate(BigDecimal.TEN)
                                                .totalBillableHours(BigDecimal.TEN)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        when(paymentFeeMethodService.getFeeMethodType(competentAuthority, requestType)).thenReturn(Optional.of(FeeMethodType.STANDARD));
        BigDecimal actualAmount = paymentDetermineDreAmountService.determineAmount(request);
        assertEquals(amount, actualAmount);
    }

    @Test
    void getAmount_fee_method_type_not_defined() {
        String requestId = "1";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        RequestType requestType = RequestType.AVIATION_DOE_CORSIA;
        Request request = Request.builder()
                .id(requestId)
                .competentAuthority(competentAuthority)
                .type(requestType)
                .payload(AviationDoECorsiaRequestPayload.builder()
                        .doe(AviationDoECorsia.builder().fee(AviationDoECorsiaFee.builder()
                                        .chargeOperator(true)
                                        .feeDetails(AviationDoECorsiaFeeDetails.builder()
                                                .hourlyRate(BigDecimal.TEN)
                                                .totalBillableHours(BigDecimal.TEN)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        when(paymentFeeMethodService.getFeeMethodType(competentAuthority, requestType)).thenReturn(Optional.empty());
        BigDecimal actualAmount = paymentDetermineDreAmountService.determineAmount(request);
        assertEquals(BigDecimal.ZERO, actualAmount);
    }

    @Test
    void getAmount_amount_not_set() {
        String requestId = "1";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        RequestType requestType = RequestType.AVIATION_DOE_CORSIA;
        Request request = Request.builder()
                .id(requestId)
                .competentAuthority(competentAuthority)
                .type(requestType)
                .build();

        when(paymentFeeMethodService.getFeeMethodType(competentAuthority, requestType)).thenReturn(Optional.empty());
        BigDecimal actualAmount = paymentDetermineDreAmountService.determineAmount(request);
        assertEquals(BigDecimal.ZERO, actualAmount);
    }

    @Test
    void getRequestTypes() {
        assertEquals(RequestType.AVIATION_DOE_CORSIA, paymentDetermineDreAmountService.getRequestType());
    }
}
