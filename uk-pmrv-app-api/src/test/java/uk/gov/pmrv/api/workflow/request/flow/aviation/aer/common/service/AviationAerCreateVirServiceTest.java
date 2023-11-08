package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationAerService;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerRecommendedImprovements;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerUncorrectedMisstatements;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerUncorrectedNonCompliances;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUncorrectedNonConformities;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirCreationService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@ExtendWith(MockitoExtension.class)
class AviationAerCreateVirServiceTest {

    @InjectMocks
    private AviationAerCreateVirService aerCreateVirService;

    @Mock
    private RequestService requestService;

    @Mock
    private AviationVirCreationService virCreationService;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private AviationAerService aerService;

    @Test
    void createRequestVir() {
        
        final long accountId = 1L;
        final String requestId = "AEM-1";
        final AviationAerUkEtsRequestPayload aerRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(true)
            .verificationReport(AviationAerUkEtsVerificationReport.builder()
                .verificationData(AviationAerUkEtsVerificationData.builder()
                    .uncorrectedMisstatements(AviationAerUncorrectedMisstatements.builder()
                        .exist(true)
                        .uncorrectedMisstatements(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                        .existUncorrectedNonConformities(true)
                        .uncorrectedNonConformities(Set.of(UncorrectedItem.builder().build()))
                        .priorYearIssues(Set.of(VerifierComment.builder().build()))
                        .build())
                    .uncorrectedNonCompliances(AviationAerUncorrectedNonCompliances.builder()
                        .exist(true)
                        .uncorrectedNonCompliances(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                        .exist(true)
                        .recommendedImprovements(Set.of(VerifierComment.builder().build()))
                        .build())
                    .build())
                .build())
            .build();
        
        final Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(aerRequestPayload)
            .metadata(AviationAerRequestMetadata.builder()
                .year(Year.now())
                .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(AviationAccountStatus.LIVE, AviationAccountStatus.NEW)))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(true).build());
        when(aerService.existsAerByAccountIdAndYear(accountId, Year.now())).thenReturn(false);

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, times(1))
            .validateAccountStatuses(accountId, Set.of(AviationAccountStatus.LIVE, AviationAccountStatus.NEW));
        verify(virCreationService, times(1)).createRequestVir(requestId);
        assertThat(((AviationAerUkEtsRequestPayload) request.getPayload()).isVirTriggered()).isTrue();
    }

    @Test
    void createRequestVir_not_triggered_when_aer_is_reinitiated() {
        
        final long accountId = 1L;
        final String requestId = "AEM-1";
        final AviationAerUkEtsRequestPayload aerRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(true)
            .verificationReport(AviationAerUkEtsVerificationReport.builder()
                .verificationData(AviationAerUkEtsVerificationData.builder()
                    .uncorrectedMisstatements(AviationAerUncorrectedMisstatements.builder()
                        .exist(true)
                        .uncorrectedMisstatements(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                        .existUncorrectedNonConformities(true)
                        .uncorrectedNonConformities(Set.of(UncorrectedItem.builder().build()))
                        .priorYearIssues(Set.of(VerifierComment.builder().build()))
                        .build())
                    .uncorrectedNonCompliances(AviationAerUncorrectedNonCompliances.builder()
                        .exist(true)
                        .uncorrectedNonCompliances(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                        .exist(true)
                        .recommendedImprovements(Set.of(VerifierComment.builder().build()))
                        .build())
                    .build())
                .build())
            .build();

        final Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(aerRequestPayload)
            .metadata(AviationAerRequestMetadata.builder()
                .year(Year.now())
                .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(AviationAccountStatus.LIVE, AviationAccountStatus.NEW)))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(true).build());
        when(aerService.existsAerByAccountIdAndYear(accountId, Year.now())).thenReturn(true);

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, times(1))
            .validateAccountStatuses(accountId, Set.of(AviationAccountStatus.LIVE, AviationAccountStatus.NEW));
        verify(virCreationService, never()).createRequestVir(anyString());
        assertThat(((AviationAerUkEtsRequestPayload) request.getPayload()).isVirTriggered()).isFalse();
    }

    @Test
    void createRequestVir_vir_not_triggered() {
        
        final String requestId = "AEM-1";
        final AviationAerUkEtsRequestPayload aerRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(false)
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(aerRequestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, never()).validateAccountStatuses(anyLong(), anySet());
        verify(virCreationService, never()).createRequestVir(anyString());
        assertThat(((AviationAerUkEtsRequestPayload) request.getPayload()).isVirTriggered()).isFalse();
    }

    @Test
    void createRequestVir_vir_not_valid_for_aer() {
        
        final long accountId = 1L;
        final String requestId = "AEM-1";
        final AviationAerUkEtsRequestPayload aerRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(true)
            .verificationReport(AviationAerUkEtsVerificationReport.builder()
                .verificationData(AviationAerUkEtsVerificationData.builder()
                    .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                        .existUncorrectedNonConformities(false)
                        .existPriorYearIssues(false)
                        .build())
                    .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                        .exist(false)
                        .build())
                    .build())
                .build())
            .build();
        
        final Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(aerRequestPayload)
            .metadata(AviationAerRequestMetadata.builder()
                .year(Year.now())
                .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(AviationAccountStatus.LIVE, AviationAccountStatus.NEW)))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(true).build());

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, times(1))
            .validateAccountStatuses(accountId, Set.of(AviationAccountStatus.LIVE, AviationAccountStatus.NEW));
        verify(virCreationService, never()).createRequestVir(anyString());
        assertThat(((AviationAerUkEtsRequestPayload) request.getPayload()).isVirTriggered()).isFalse();
    }

    @Test
    void createRequestVir_not_valid_account() {
        
        final long accountId = 1L;
        final String requestId = "AEM-1";
        final AviationAerUkEtsRequestPayload aerRequestPayload = AviationAerUkEtsRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(true)
            .verificationReport(AviationAerUkEtsVerificationReport.builder()
                .verificationData(AviationAerUkEtsVerificationData.builder()
                    .uncorrectedMisstatements(AviationAerUncorrectedMisstatements.builder()
                        .exist(true)
                        .uncorrectedMisstatements(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .uncorrectedNonConformities(AviationAerUncorrectedNonConformities.builder()
                        .existUncorrectedNonConformities(true)
                        .uncorrectedNonConformities(Set.of(UncorrectedItem.builder().build()))
                        .priorYearIssues(Set.of(VerifierComment.builder().build()))
                        .build())
                    .uncorrectedNonCompliances(AviationAerUncorrectedNonCompliances.builder()
                        .exist(true)
                        .uncorrectedNonCompliances(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .recommendedImprovements(AviationAerRecommendedImprovements.builder()
                        .exist(true)
                        .recommendedImprovements(Set.of(VerifierComment.builder().build()))
                        .build())
                    .build())
                .build())
            .build();
        
        final Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(aerRequestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(AviationAccountStatus.LIVE, AviationAccountStatus.NEW)))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(false).build());

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, times(1))
            .validateAccountStatuses(accountId, Set.of(AviationAccountStatus.LIVE, AviationAccountStatus.NEW));
        verify(virCreationService, never()).createRequestVir(anyString());
        assertThat(((AviationAerUkEtsRequestPayload) request.getPayload()).isVirTriggered()).isFalse();
    }
}
