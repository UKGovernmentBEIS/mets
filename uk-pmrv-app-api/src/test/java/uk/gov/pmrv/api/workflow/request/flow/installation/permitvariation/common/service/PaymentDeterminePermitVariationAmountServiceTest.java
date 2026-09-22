package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.service.FeePaymentService;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PaymentVariationManualProperties;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRequestPaymentDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PaymentDeterminePermitVariationAmountServiceTest {

    private static final Long ACCOUNT_ID = 1L;

    @Mock
    private PaymentFeeMethodService paymentFeeMethodService;

    @Mock
    private PaymentVariationManualProperties paymentVariationManualProperties;

    @Mock
    private FeePaymentService feePaymentService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private InstallationAccountInfoDTO installationAccountInfoDTO;

    private PaymentDeterminePermitVariationAmountService service;

    @BeforeEach
    void setUp() {
        service = new PaymentDeterminePermitVariationAmountService(
                paymentFeeMethodService,
                paymentVariationManualProperties,
                List.of(feePaymentService),
                installationAccountQueryService
        );
    }

    @Test
    void determineAmount_shouldReturnZero_whenFeeMethodTypeDoesNotExist() {
        Request request = buildRequest(
                CompetentAuthorityEnum.WALES,
                PermitVariationRequestPayload.builder().build()
        );

        mockEmitterType(request, EmitterType.GHGE);

        when(paymentFeeMethodService.getFeeMethodType(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION
        )).thenReturn(Optional.empty());

        BigDecimal result = service.determineAmount(request);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);

        verifyNoInteractions(paymentVariationManualProperties);
        verifyNoInteractions(feePaymentService);
    }

    @Test
    void determineAmount_shouldReturnPayloadAmount_whenManualPaymentIsPendingForFixedFee() {
        BigDecimal amount = BigDecimal.valueOf(150);

        PermitVariationRequestPaymentDetails paymentDetails =
                PermitVariationRequestPaymentDetails.builder()
                        .amount(amount)
                        .manualPaymentInitiationPending(true)
                        .build();

        PermitVariationRequestPayload requestPayload =
                PermitVariationRequestPayload.builder()
                        .requestPaymentDetails(paymentDetails)
                        .build();

        Request request = buildRequest(
                CompetentAuthorityEnum.WALES,
                requestPayload
        );

        mockEmitterType(request, EmitterType.GHGE);
        mockFeeMethodType(request, FeeMethodType.STANDARD);
        mockManualPaymentEnabled(request, FeeType.FIXED);

        BigDecimal result = service.determineAmount(request);

        assertThat(result).isEqualByComparingTo(amount);
        assertThat(paymentDetails.getManualPaymentInitiationPending()).isFalse();

        verify(paymentFeeMethodService)
                .isZeroNonChangeableFeeConfigured(
                        CompetentAuthorityEnum.WALES,
                        RequestType.PERMIT_VARIATION,
                        FeeType.FIXED
                );

        verifyNoInteractions(feePaymentService);
    }

    @Test
    void determineAmount_shouldReturnPayloadAmount_whenManualPaymentIsPendingForWasteFee() {
        BigDecimal amount = BigDecimal.valueOf(150);

        PermitVariationRequestPaymentDetails paymentDetails =
                PermitVariationRequestPaymentDetails.builder()
                        .amount(amount)
                        .manualPaymentInitiationPending(true)
                        .build();

        PermitVariationRequestPayload requestPayload =
                PermitVariationRequestPayload.builder()
                        .requestPaymentDetails(paymentDetails)
                        .build();

        Request request = buildRequest(
                CompetentAuthorityEnum.WALES,
                requestPayload
        );

        mockEmitterType(request, EmitterType.WASTE);
        mockFeeMethodType(request, FeeMethodType.STANDARD);
        mockManualPaymentEnabled(request, FeeType.WASTE);

        BigDecimal result = service.determineAmount(request);

        assertThat(result).isEqualByComparingTo(amount);
        assertThat(paymentDetails.getManualPaymentInitiationPending()).isFalse();

        verify(paymentFeeMethodService)
                .isZeroNonChangeableFeeConfigured(
                        CompetentAuthorityEnum.WALES,
                        RequestType.PERMIT_VARIATION,
                        FeeType.WASTE
                );

        verifyNoInteractions(feePaymentService);
    }

    @Test
    void determineAmount_shouldReturnZero_whenManualPaymentIsNotPending() {
        PermitVariationRequestPaymentDetails paymentDetails =
                PermitVariationRequestPaymentDetails.builder()
                        .amount(BigDecimal.valueOf(150))
                        .manualPaymentInitiationPending(false)
                        .build();

        PermitVariationRequestPayload requestPayload =
                PermitVariationRequestPayload.builder()
                        .requestPaymentDetails(paymentDetails)
                        .build();

        Request request = buildRequest(
                CompetentAuthorityEnum.WALES,
                requestPayload
        );

        mockEmitterType(request, EmitterType.GHGE);
        mockFeeMethodType(request, FeeMethodType.STANDARD);
        mockManualPaymentEnabled(request, FeeType.FIXED);

        BigDecimal result = service.determineAmount(request);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(paymentDetails.getManualPaymentInitiationPending()).isFalse();

        verifyNoInteractions(feePaymentService);
    }

    @Test
    void determineAmount_shouldReturnZero_whenManualPaymentDetailsDoNotExist() {
        PermitVariationRequestPayload requestPayload =
                PermitVariationRequestPayload.builder()
                        .build();

        Request request = buildRequest(
                CompetentAuthorityEnum.WALES,
                requestPayload
        );

        mockEmitterType(request, EmitterType.GHGE);
        mockFeeMethodType(request, FeeMethodType.STANDARD);
        mockManualPaymentEnabled(request, FeeType.FIXED);

        BigDecimal result = service.determineAmount(request);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);

        verifyNoInteractions(feePaymentService);
    }

    @Test
    void determineAmount_shouldConsumeManualPaymentAmountOnlyOnce() {
        BigDecimal amount = BigDecimal.valueOf(150);

        PermitVariationRequestPaymentDetails paymentDetails =
                PermitVariationRequestPaymentDetails.builder()
                        .amount(amount)
                        .manualPaymentInitiationPending(true)
                        .build();

        PermitVariationRequestPayload requestPayload =
                PermitVariationRequestPayload.builder()
                        .requestPaymentDetails(paymentDetails)
                        .build();

        Request request = buildRequest(
                CompetentAuthorityEnum.WALES,
                requestPayload
        );

        mockEmitterType(request, EmitterType.GHGE);
        mockFeeMethodType(request, FeeMethodType.STANDARD);
        mockManualPaymentEnabled(request, FeeType.FIXED);

        BigDecimal firstResult = service.determineAmount(request);
        BigDecimal secondResult = service.determineAmount(request);

        assertThat(firstResult).isEqualByComparingTo(amount);
        assertThat(secondResult).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(paymentDetails.getManualPaymentInitiationPending()).isFalse();

        verifyNoInteractions(feePaymentService);
    }

    @Test
    void determineAmount_shouldReturnFeePaymentServiceAmount_whenCompetentAuthorityIsNotConfiguredForManualPayment() {
        BigDecimal amount = BigDecimal.valueOf(250);
        FeeMethodType feeMethodType = FeeMethodType.STANDARD;

        Request request = buildRequest(
                CompetentAuthorityEnum.ENGLAND,
                PermitVariationRequestPayload.builder().build()
        );

        mockEmitterType(request, EmitterType.GHGE);
        mockFeeMethodType(request, feeMethodType);

        when(paymentVariationManualProperties.getCas())
                .thenReturn(Set.of(CompetentAuthorityEnum.WALES));

        when(feePaymentService.getFeeMethodType())
                .thenReturn(feeMethodType);

        when(feePaymentService.getAmount(request))
                .thenReturn(amount);

        BigDecimal result = service.determineAmount(request);

        assertThat(result).isEqualByComparingTo(amount);

        verify(paymentFeeMethodService, never())
                .isZeroNonChangeableFeeConfigured(
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void determineAmount_shouldReturnFeePaymentServiceAmount_whenZeroNonChangeableFeeIsNotConfigured() {
        BigDecimal amount = BigDecimal.valueOf(250);
        FeeMethodType feeMethodType = FeeMethodType.STANDARD;

        Request request = buildRequest(
                CompetentAuthorityEnum.WALES,
                PermitVariationRequestPayload.builder().build()
        );

        mockEmitterType(request, EmitterType.GHGE);
        mockFeeMethodType(request, feeMethodType);

        when(paymentVariationManualProperties.getCas())
                .thenReturn(Set.of(CompetentAuthorityEnum.WALES));

        when(paymentFeeMethodService.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.FIXED
        )).thenReturn(false);

        when(feePaymentService.getFeeMethodType())
                .thenReturn(feeMethodType);

        when(feePaymentService.getAmount(request))
                .thenReturn(amount);

        BigDecimal result = service.determineAmount(request);

        assertThat(result).isEqualByComparingTo(amount);
    }

    @Test
    void determineAmount_shouldThrowException_whenNoFeePaymentServiceMatchesFeeMethodType() {
        FeeMethodType feeMethodType = FeeMethodType.STANDARD;

        Request request = buildRequest(
                CompetentAuthorityEnum.ENGLAND,
                PermitVariationRequestPayload.builder().build()
        );

        mockEmitterType(request, EmitterType.GHGE);
        mockFeeMethodType(request, feeMethodType);

        when(paymentVariationManualProperties.getCas())
                .thenReturn(Set.of(CompetentAuthorityEnum.WALES));

        when(feePaymentService.getFeeMethodType())
                .thenReturn(FeeMethodType.INSTALLATION_CATEGORY_BASED);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.determineAmount(request)
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.FEE_CONFIGURATION_NOT_EXIST);
    }

    @Test
    void getRequestType_shouldReturnPermitVariation() {
        assertThat(service.getRequestType())
                .isEqualTo(RequestType.PERMIT_VARIATION);
    }

    private Request buildRequest(
            CompetentAuthorityEnum competentAuthority,
            PermitVariationRequestPayload payload) {

        return Request.builder()
                .accountId(ACCOUNT_ID)
                .type(RequestType.PERMIT_VARIATION)
                .competentAuthority(competentAuthority)
                .payload(payload)
                .build();
    }

    private void mockEmitterType(
            Request request,
            EmitterType emitterType) {

        when(installationAccountQueryService
                .getInstallationAccountInfoDTOById(request.getAccountId()))
                .thenReturn(installationAccountInfoDTO);

        when(installationAccountInfoDTO.getEmitterType())
                .thenReturn(emitterType);
    }

    private void mockFeeMethodType(
            Request request,
            FeeMethodType feeMethodType) {

        when(paymentFeeMethodService.getFeeMethodType(
                request.getCompetentAuthority(),
                request.getType()
        )).thenReturn(Optional.of(feeMethodType));
    }

    private void mockManualPaymentEnabled(
            Request request,
            FeeType feeType) {

        when(paymentVariationManualProperties.getCas())
                .thenReturn(Set.of(request.getCompetentAuthority()));

        when(paymentFeeMethodService.isZeroNonChangeableFeeConfigured(
                request.getCompetentAuthority(),
                request.getType(),
                feeType
        )).thenReturn(true);
    }
}
