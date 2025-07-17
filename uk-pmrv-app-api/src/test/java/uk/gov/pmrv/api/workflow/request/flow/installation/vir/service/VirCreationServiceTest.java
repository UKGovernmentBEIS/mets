package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.UncorrectedNonConformities;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.validation.VirCreationValidatorService;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirCreationServiceTest {

    @InjectMocks
    private VirCreationService virCreationService;

    @Mock
    private RequestService requestService;

    @Mock
    private VirDueDateService virDueDateService;

    @Mock
    private VirCreationValidatorService virCreationValidatorService;

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
        final Map<String, UncorrectedItem> UncorrectedItems = Map.of("A1", uncorrectedItem);
        final AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .verificationReport(AerVerificationReport.builder()
                        .verificationData(AerVerificationData.builder()
                                .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                                        .uncorrectedNonConformities(Set.of(uncorrectedItem))
                                        .build())
                                .build())
                        .build())
                .permitOriginatedData(PermitOriginatedData.builder()
                        .permitType(PermitType.GHGE)
                        .installationCategory(InstallationCategory.A)
                        .build())
                .build();
        final Request aerRequest = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .metadata(AerRequestMetadata.builder().year(year).build())
                .payload(aerRequestPayload)
                .creationDate(LocalDateTime.of(2023, 5, 14, 5, 40))
                .build();

        final VirVerificationData virVerificationData = VirVerificationData.builder()
                .uncorrectedNonConformities(UncorrectedItems)
                .build();
        final RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();
        final VirRequestPayload virRequestPayload = VirRequestPayload.builder()
                .payloadType(RequestPayloadType.VIR_REQUEST_PAYLOAD)
                .verificationData(virVerificationData)
                .build();
        final RequestParams params = RequestParams.builder()
                .type(RequestType.VIR)
                .accountId(accountId)
                .requestPayload(virRequestPayload)
                .requestMetadata(VirRequestMetadata.builder()
                        .type(RequestMetadataType.VIR)
                        .year(year)
                        .relatedAerRequestId(requestId)
                        .build())
                .processVars(Map.of(BpmnProcessConstants.VIR_EXPIRATION_DATE, dueDate))
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(aerRequest);
        when(virCreationValidatorService.validate(virVerificationData, accountId, year, PermitType.GHGE))
                .thenReturn(validationResult);
        when(virDueDateService.generateDueDate(Year.of(2023))).thenReturn(dueDate);

        // Invoke
        virCreationService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(virCreationValidatorService, times(1))
                .validate(virVerificationData, accountId, year, PermitType.GHGE);
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
        final AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                .verificationReport(AerVerificationReport.builder()
                        .verificationData(AerVerificationData.builder()
                                .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                                        .uncorrectedNonConformities(Set.of(uncorrectedItem))
                                        .build())
                                .build())
                        .build())
                .permitOriginatedData(PermitOriginatedData.builder()
                        .permitType(PermitType.GHGE)
                        .installationCategory(InstallationCategory.A)
                        .build())
                .build();
        final Request aerRequest = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .metadata(AerRequestMetadata.builder().year(year).build())
                .payload(aerRequestPayload)
                .build();

        final VirVerificationData virVerificationData = VirVerificationData.builder()
                .uncorrectedNonConformities(Map.of("A1", uncorrectedItem))
                .build();
        final RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(false).build();

        when(requestService.findRequestById(requestId)).thenReturn(aerRequest);
        when(virCreationValidatorService.validate(virVerificationData, accountId, year, PermitType.GHGE))
                .thenReturn(validationResult);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                virCreationService.createRequestVir(requestId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(MetsErrorCode.VIR_CREATION_NOT_ALLOWED);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(virCreationValidatorService, times(1))
                .validate(virVerificationData, accountId, year, PermitType.GHGE);
        verify(virDueDateService, never()).generateDueDate(any());
        verify(startProcessRequestService, never()).startProcess(any());
    }
}
