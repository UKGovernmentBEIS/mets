package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.service.FeePaymentService;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.payment.service.StandardFeePaymentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class PaymentDetermineAmountDefaultServiceTest {

    private PaymentDetermineAmountDefaultService cut;

    @Mock
    private PaymentFeeMethodService paymentFeeMethodService;
    
    @Mock
    private StandardFeePaymentService standardFeePaymentService;

    @BeforeEach
    void setUp() {
        List<FeePaymentService> feePaymentServices = List.of(standardFeePaymentService);
        cut = new PaymentDetermineAmountDefaultService(paymentFeeMethodService, feePaymentServices);
    }

	@Test
	void determineAmount() {
		String requestId = "1";
		CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
		RequestType requestType = RequestType.PERMIT_ISSUANCE;
		Request request = Request.builder().id(requestId).competentAuthority(competentAuthority).type(requestType)
				.payload(PermitIssuanceRequestPayload.builder()
						.payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD).build())
				.build();
		FeeMethodType feeMethodType = FeeMethodType.STANDARD;
		BigDecimal expectedAmount = BigDecimal.valueOf(234.5);

		when(paymentFeeMethodService.getFeeMethodType(competentAuthority, requestType))
				.thenReturn(Optional.of(feeMethodType));
		when(standardFeePaymentService.getFeeMethodType()).thenReturn(FeeMethodType.STANDARD);
		when(standardFeePaymentService.getAmount(request)).thenReturn(expectedAmount);

		BigDecimal actualAmount = cut.determineAmount(request);

		assertEquals(expectedAmount, actualAmount);

		verify(paymentFeeMethodService, times(1)).getFeeMethodType(competentAuthority, requestType);
		verify(standardFeePaymentService, times(1)).getFeeMethodType();
		verify(standardFeePaymentService, times(1)).getAmount(request);
		verifyNoMoreInteractions(standardFeePaymentService);
	}
	
	@Test
	void determineAmount_no_feeMethodType_found() {
		String requestId = "1";
		CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
		RequestType requestType = RequestType.PERMIT_TRANSFER_A;
		Request request = Request.builder().id(requestId).competentAuthority(competentAuthority).type(requestType)
				.build();

		when(paymentFeeMethodService.getFeeMethodType(competentAuthority, requestType)).thenReturn(Optional.empty());

		BigDecimal actualAmount = cut.determineAmount(request);

		assertEquals(BigDecimal.ZERO, actualAmount);
		verify(paymentFeeMethodService, times(1)).getFeeMethodType(competentAuthority, requestType);
	}

	@Test
	void determineAmount_no_service_found() {
		String requestId = "1";
		CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
		RequestType requestType = RequestType.PERMIT_TRANSFER_A;
		Request request = Request.builder().id(requestId).competentAuthority(competentAuthority).type(requestType)
				.build();
		FeeMethodType feeMethodType = FeeMethodType.INSTALLATION_CATEGORY_BASED;

		when(paymentFeeMethodService.getFeeMethodType(competentAuthority, requestType))
				.thenReturn(Optional.of(feeMethodType));
		when(standardFeePaymentService.getFeeMethodType()).thenReturn(FeeMethodType.STANDARD);

		BusinessException be = assertThrows(BusinessException.class, () -> cut.determineAmount(request));

		assertEquals(ErrorCode.FEE_CONFIGURATION_NOT_EXIST, be.getErrorCode());

		verify(paymentFeeMethodService, times(1)).getFeeMethodType(competentAuthority, requestType);
		verify(standardFeePaymentService, times(1)).getFeeMethodType();
	}
}
