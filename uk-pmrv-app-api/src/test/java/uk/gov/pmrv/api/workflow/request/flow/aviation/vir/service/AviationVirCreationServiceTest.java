package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUncorrectedNonConformities;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.validation.AviationVirCreationValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@ExtendWith(MockitoExtension.class)
class AviationVirCreationServiceTest {

    @InjectMocks
    private AviationVirCreationService virCreationService;

    @Mock
    private RequestService requestService;

    @Mock
    private AviationVirDueDateService virDueDateService;

    @Mock
    private AviationVirCreationValidator virCreationValidator;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequestVir() {

        final String requestId = "AEM-1";
        final Long accountId = 1L;
        final Year year = Year.of(2022);
        final Date dueDate = new Date();
        final UncorrectedItem uncorrectedItem = UncorrectedItem.builder()
                .explanation("Explanation")
                .reference("A1")
                .materialEffect(true)
                .build();
        final Map<String, UncorrectedItem> uncorrectedItems = Map.of("A1", uncorrectedItem);
        final AviationAerUkEtsRequestPayload aerRequestPayload = AviationAerUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
                .verificationReport(AviationAerUkEtsVerificationReport.builder()
                        .verificationData(AviationAerUkEtsVerificationData.builder()
                                .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                                        .uncorrectedNonConformities(Set.of(uncorrectedItem))
                                        .build())
                                .build())
                        .build())
                .build();

        final Request aerRequest = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .metadata(AviationAerRequestMetadata.builder().year(year).build())
                .payload(aerRequestPayload)
                .creationDate(LocalDateTime.of(2023, 5, 14, 5, 40))
                .build();

        final VirVerificationData virVerificationData = VirVerificationData.builder()
                .uncorrectedNonConformities(uncorrectedItems)
                .build();

        final RequestCreateValidationResult validationResult =
                RequestCreateValidationResult.builder().valid(true).build();
        final AviationVirRequestPayload virRequestPayload = AviationVirRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_VIR_REQUEST_PAYLOAD)
                .verificationData(virVerificationData)
                .build();

        final RequestParams params = RequestParams.builder()
                .type(RequestType.AVIATION_VIR)
                .accountId(accountId)
                .requestPayload(virRequestPayload)
                .requestMetadata(AviationVirRequestMetadata.builder()
                        .type(RequestMetadataType.AVIATION_VIR)
                        .year(year)
                        .relatedAerRequestId(requestId)
                        .build())
                .processVars(Map.of(BpmnProcessConstants.AVIATION_VIR_EXPIRATION_DATE, dueDate))
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(aerRequest);
        when(virCreationValidator.validate(virVerificationData, accountId, year)).thenReturn(validationResult);
        when(virDueDateService.generateDueDate(Year.of(2023))).thenReturn(dueDate);

        // Invoke
        virCreationService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(virCreationValidator, times(1)).validate(virVerificationData, accountId, year);
        verify(virDueDateService, times(1)).generateDueDate(Year.of(2023));
        verify(startProcessRequestService, times(1)).startProcess(params);
    }

    @Test
    void createRequestVir_not_allowed() {

        final String requestId = "AEM-1";
        final Long accountId = 1L;
        final Year year = Year.of(2022);
        final UncorrectedItem uncorrectedItem = UncorrectedItem.builder()
                .explanation("Explanation")
                .reference("A1")
                .materialEffect(true)
                .build();
        final Map<String, UncorrectedItem> uncorrectedItems = Map.of("A1", uncorrectedItem);
        final AviationAerUkEtsRequestPayload aerRequestPayload = AviationAerUkEtsRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_AER_UKETS_REQUEST_PAYLOAD)
                .verificationReport(AviationAerUkEtsVerificationReport.builder()
                        .verificationData(AviationAerUkEtsVerificationData.builder()
                                .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                                        .uncorrectedNonConformities(Set.of(uncorrectedItem))
                                        .build())
                                .build())
                        .build())
                .build();

        final Request aerRequest = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .metadata(AviationAerRequestMetadata.builder().year(year).build())
                .payload(aerRequestPayload)
                .build();


        final VirVerificationData virVerificationData = VirVerificationData.builder()
                .uncorrectedNonConformities(uncorrectedItems)
                .build();

        final RequestCreateValidationResult validationResult =
                RequestCreateValidationResult.builder().valid(false).build();

        when(requestService.findRequestById(requestId)).thenReturn(aerRequest);
        when(virCreationValidator.validate(virVerificationData, accountId, year)).thenReturn(validationResult);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                virCreationService.createRequestVir(requestId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.VIR_CREATION_NOT_ALLOWED);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(virCreationValidator, times(1)).validate(virVerificationData, accountId, year);
        verify(virDueDateService, never()).generateDueDate(any());
        verify(startProcessRequestService, never()).startProcess(any());
    }
}
