package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

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
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PaymentVariationManualProperties;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentStatus;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PermitVariationRequestServiceTest {

    private static final String REQUEST_ID = "1";

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private PaymentVariationManualProperties paymentVariationManualProperties;

    @Mock
    private PaymentFeeMethodService paymentFeeMethodService;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private WorkflowService workflowService;

    private PermitVariationRequestService service;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private InstallationAccountInfoDTO installationAccountInfoDTO;

    @BeforeEach
    void setUp() {
        service = new PermitVariationRequestService(
                requestRepository,
                paymentVariationManualProperties,
                paymentFeeMethodService,
                requestTaskRepository,
                workflowService,
                installationAccountQueryService
        );
    }

    @Test
    void canRequestPayment_shouldReturnTrue_whenNoPreviousPaymentExists() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                null
        );

        mockManualPaymentEnabled(request, EmitterType.GHGE);
        mockPaymentInitiationSubscriptionExists();

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of());

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isTrue();
    }

    @Test
    void canRequestPayment_shouldReturnTrue_whenPreviousPaymentIsCancelled() {
        RequestPaymentInfo paymentInfo = RequestPaymentInfo.builder()
                .status(PaymentStatus.CANCELLED)
                .build();

        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                paymentInfo
        );

        mockManualPaymentEnabled(request, EmitterType.GHGE);
        mockPaymentInitiationSubscriptionExists();

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of());

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isTrue();
    }

    @Test
    void canRequestPayment_shouldReturnFalse_whenPreviousPaymentIsCompleted() {
        RequestPaymentInfo paymentInfo = RequestPaymentInfo.builder()
                .status(PaymentStatus.COMPLETED)
                .build();

        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                paymentInfo
        );

        mockManualPaymentEnabled(request, EmitterType.GHGE);

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of());

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isFalse();

        verifyNoInteractions(workflowService);
    }

    @Test
    void canRequestPayment_shouldReturnFalse_whenPreviousPaymentIsMarkedAsReceived() {
        RequestPaymentInfo paymentInfo = RequestPaymentInfo.builder()
                .status(PaymentStatus.MARK_AS_RECEIVED)
                .build();

        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                paymentInfo
        );

        mockManualPaymentEnabled(request, EmitterType.GHGE);

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of());

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isFalse();

        verifyNoInteractions(workflowService);
    }

    @Test
    void canRequestPayment_shouldReturnFalse_whenMakePaymentTaskExists() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                null
        );

        RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.PERMIT_VARIATION_MAKE_PAYMENT)
                .build();

        mockManualPaymentEnabled(request, EmitterType.GHGE);

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of(requestTask));

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isFalse();

        verifyNoInteractions(workflowService);
    }

    @Test
    void canRequestPayment_shouldReturnFalse_whenTrackPaymentTaskExists() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                null
        );

        RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.PERMIT_VARIATION_TRACK_PAYMENT)
                .build();

        mockManualPaymentEnabled(request, EmitterType.GHGE);

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of(requestTask));

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isFalse();

        verifyNoInteractions(workflowService);
    }

    @Test
    void canRequestPayment_shouldReturnFalse_whenConfirmPaymentTaskExists() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                null
        );

        RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.PERMIT_VARIATION_CONFIRM_PAYMENT)
                .build();

        mockManualPaymentEnabled(request, EmitterType.GHGE);

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of(requestTask));

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isFalse();

        verifyNoInteractions(workflowService);
    }

    @Test
    void canRequestPayment_shouldReturnTrue_whenUnrelatedTaskExists() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                null
        );

        RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW)
                .build();

        mockManualPaymentEnabled(request, EmitterType.GHGE);
        mockPaymentInitiationSubscriptionExists();

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of(requestTask));

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isTrue();
    }

    @Test
    void canRequestPayment_shouldReturnFalse_whenPaymentInitiationSubscriptionDoesNotExist() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                null
        );

        mockManualPaymentEnabled(request, EmitterType.GHGE);

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of());

        when(workflowService.hasMessageEventSubscriptionWithName(
                REQUEST_ID,
                BpmnProcessConstants.PERMIT_VARIATION_INITIATE_PAYMENT
        )).thenReturn(false);

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isFalse();
    }

    @Test
    void canRequestPayment_shouldReturnFalse_whenCompetentAuthorityIsNotConfiguredForManualPayment() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.ENGLAND,
                null
        );

        when(installationAccountQueryService
                .getInstallationAccountInfoDTOById(request.getAccountId()))
                .thenReturn(installationAccountInfoDTO);

        when(installationAccountInfoDTO.getEmitterType())
                .thenReturn(EmitterType.GHGE);

        when(paymentVariationManualProperties.getCas())
                .thenReturn(Set.of(CompetentAuthorityEnum.WALES));

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isFalse();

        verifyNoInteractions(paymentFeeMethodService);
        verifyNoInteractions(requestTaskRepository);
        verifyNoInteractions(workflowService);
    }

    @Test
    void canRequestPayment_shouldReturnFalse_whenManualPaymentFeeIsNotConfigured() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                null
        );

        when(installationAccountQueryService
                .getInstallationAccountInfoDTOById(request.getAccountId()))
                .thenReturn(installationAccountInfoDTO);

        when(installationAccountInfoDTO.getEmitterType())
                .thenReturn(EmitterType.GHGE);

        when(paymentVariationManualProperties.getCas())
                .thenReturn(Set.of(CompetentAuthorityEnum.WALES));

        when(paymentFeeMethodService.isZeroNonChangeableFeeConfigured(
                CompetentAuthorityEnum.WALES,
                RequestType.PERMIT_VARIATION,
                FeeType.FIXED
        )).thenReturn(false);

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isFalse();

        verifyNoInteractions(requestTaskRepository);
        verifyNoInteractions(workflowService);
    }

    @Test
    void canRequestPayment_shouldReturnFalse_whenRequestIsNotInProgress() {
        buildRequest(
                RequestStatus.COMPLETED,
                CompetentAuthorityEnum.WALES,
                null
        );

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isFalse();

        verifyNoInteractions(paymentVariationManualProperties);
        verifyNoInteractions(paymentFeeMethodService);
        verifyNoInteractions(requestTaskRepository);
        verifyNoInteractions(workflowService);
    }

    @Test
    void canRequestPayment_shouldThrowResourceNotFound_whenRequestDoesNotExist() {
        when(requestRepository.findById(REQUEST_ID))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.canRequestPayment(REQUEST_ID)
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verifyNoInteractions(paymentVariationManualProperties);
        verifyNoInteractions(paymentFeeMethodService);
        verifyNoInteractions(requestTaskRepository);
        verifyNoInteractions(workflowService);
    }

    @Test
    void canRequestPayment_shouldUseWasteFeeType_whenEmitterTypeIsWaste() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                null
        );

        mockManualPaymentEnabled(request, EmitterType.WASTE);
        mockPaymentInitiationSubscriptionExists();

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of());

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isTrue();

        verify(paymentFeeMethodService)
                .isZeroNonChangeableFeeConfigured(
                        CompetentAuthorityEnum.WALES,
                        RequestType.PERMIT_VARIATION,
                        FeeType.WASTE
                );
    }

    @Test
    void canRequestPayment_shouldUseFixedFeeType_whenEmitterTypeIsNotWaste() {
        Request request = buildRequest(
                RequestStatus.IN_PROGRESS,
                CompetentAuthorityEnum.WALES,
                null
        );

        mockManualPaymentEnabled(request, EmitterType.GHGE);
        mockPaymentInitiationSubscriptionExists();

        when(requestTaskRepository.findByRequestId(REQUEST_ID))
                .thenReturn(List.of());

        boolean result = service.canRequestPayment(REQUEST_ID);

        assertThat(result).isTrue();

        verify(paymentFeeMethodService)
                .isZeroNonChangeableFeeConfigured(
                        CompetentAuthorityEnum.WALES,
                        RequestType.PERMIT_VARIATION,
                        FeeType.FIXED
                );
    }

    private Request buildRequest(
            RequestStatus status,
            CompetentAuthorityEnum competentAuthority,
            RequestPaymentInfo paymentInfo) {

        PermitVariationRequestPayload payload =
                PermitVariationRequestPayload.builder()
                        .requestPaymentInfo(paymentInfo)
                        .build();

        Request request = Request.builder()
                .id(REQUEST_ID)
                .accountId(1L)
                .status(status)
                .competentAuthority(competentAuthority)
                .payload(payload)
                .build();

        when(requestRepository.findById(REQUEST_ID))
                .thenReturn(Optional.of(request));

        return request;
    }

    private void mockManualPaymentEnabled(
            Request request,
            EmitterType emitterType) {

        when(installationAccountQueryService
                .getInstallationAccountInfoDTOById(request.getAccountId()))
                .thenReturn(installationAccountInfoDTO);

        when(installationAccountInfoDTO.getEmitterType())
                .thenReturn(emitterType);

        when(paymentVariationManualProperties.getCas())
                .thenReturn(Set.of(request.getCompetentAuthority()));

        when(paymentFeeMethodService.isZeroNonChangeableFeeConfigured(
                request.getCompetentAuthority(),
                RequestType.PERMIT_VARIATION,
                EmitterType.WASTE.equals(emitterType)
                        ? FeeType.WASTE
                        : FeeType.FIXED
        )).thenReturn(true);
    }

    private void mockPaymentInitiationSubscriptionExists() {
        when(workflowService.hasMessageEventSubscriptionWithName(
                REQUEST_ID,
                BpmnProcessConstants.PERMIT_VARIATION_INITIATE_PAYMENT
        )).thenReturn(true);
    }
}
