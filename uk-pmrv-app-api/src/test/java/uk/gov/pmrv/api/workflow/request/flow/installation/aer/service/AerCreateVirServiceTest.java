package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.RecommendedImprovements;
import uk.gov.pmrv.api.reporting.domain.verification.UncorrectedMisstatements;
import uk.gov.pmrv.api.reporting.domain.verification.UncorrectedNonCompliances;
import uk.gov.pmrv.api.reporting.domain.verification.UncorrectedNonConformities;
import uk.gov.pmrv.api.reporting.service.AerService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirCreationService;

import java.time.Year;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCreateVirServiceTest {

    @InjectMocks
    private AerCreateVirService aerCreateVirService;

    @Mock
    private RequestService requestService;

    @Mock
    private VirCreationService virCreationService;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private AerService aerService;

    @Test
    void createRequestVir() {
        final long accountId = 1L;
        final String requestId = "AEM-1";
        AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(true)
            .permitOriginatedData(PermitOriginatedData.builder()
                .permitType(PermitType.GHGE)
                .installationCategory(InstallationCategory.A)
                .build())
            .verificationReport(AerVerificationReport.builder()
                .verificationData(AerVerificationData.builder()
                    .uncorrectedMisstatements(UncorrectedMisstatements.builder()
                        .areThereUncorrectedMisstatements(true)
                        .uncorrectedMisstatements(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                        .areThereUncorrectedNonConformities(true)
                        .uncorrectedNonConformities(Set.of(UncorrectedItem.builder().build()))
                        .areTherePriorYearIssues(true)
                        .priorYearIssues(Set.of(VerifierComment.builder().build()))
                        .build())
                    .uncorrectedNonCompliances(UncorrectedNonCompliances.builder()
                        .areThereUncorrectedNonCompliances(true)
                        .uncorrectedNonCompliances(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .recommendedImprovements(RecommendedImprovements.builder()
                        .areThereRecommendedImprovements(true)
                        .recommendedImprovements(Set.of(VerifierComment.builder().build()))
                        .build())
                    .build())
                .build())
            .build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(aerRequestPayload)
            .metadata(AerRequestMetadata.builder()
                .year(Year.now())
                .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE)))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(true).build());
        when(aerService.existsAerByAccountIdAndYear(accountId, Year.now())).thenReturn(false);

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, times(1))
            .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));
        verify(virCreationService, times(1)).createRequestVir(requestId);
        assertThat(((AerRequestPayload) request.getPayload()).isVirTriggered()).isTrue();
    }

    @Test
    void createRequestVir_not_triggered_when_aer_is_reinitiated() {
        final long accountId = 1L;
        final String requestId = "AEM-1";
        AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(true)
            .permitOriginatedData(PermitOriginatedData.builder()
                .permitType(PermitType.GHGE)
                .installationCategory(InstallationCategory.A)
                .build())
            .verificationReport(AerVerificationReport.builder()
                .verificationData(AerVerificationData.builder()
                    .uncorrectedMisstatements(UncorrectedMisstatements.builder()
                        .areThereUncorrectedMisstatements(true)
                        .uncorrectedMisstatements(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                        .areThereUncorrectedNonConformities(true)
                        .uncorrectedNonConformities(Set.of(UncorrectedItem.builder().build()))
                        .areTherePriorYearIssues(true)
                        .priorYearIssues(Set.of(VerifierComment.builder().build()))
                        .build())
                    .uncorrectedNonCompliances(UncorrectedNonCompliances.builder()
                        .areThereUncorrectedNonCompliances(true)
                        .uncorrectedNonCompliances(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .recommendedImprovements(RecommendedImprovements.builder()
                        .areThereRecommendedImprovements(true)
                        .recommendedImprovements(Set.of(VerifierComment.builder().build()))
                        .build())
                    .build())
                .build())
            .build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(aerRequestPayload)
            .metadata(AerRequestMetadata.builder()
                .year(Year.now())
                .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE)))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(true).build());
        when(aerService.existsAerByAccountIdAndYear(accountId, Year.now())).thenReturn(true);

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, times(1))
            .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));
        verify(virCreationService, never()).createRequestVir(anyString());
        assertThat(((AerRequestPayload) request.getPayload()).isVirTriggered()).isFalse();
    }

    @Test
    void createRequestVir_vir_not_triggered() {
        final String requestId = "AEM-1";
        AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(false)
            .build();
        Request request = Request.builder()
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
        assertThat(((AerRequestPayload) request.getPayload()).isVirTriggered()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideAerVerificationDataCatALowEmitterInvalidAer")
    void createRequestVir_vir_not_valid_for_aer_for_cat_a_low_emitter(
        boolean uncorrectedMisstatements, boolean uncorrectNonConformities,
        boolean areTherePriorYearIssues, boolean areThereUncorrectedNonCompliances,
        boolean areThereRecommendedImprovements) {
        final long accountId = 1L;
        final String requestId = "AEM-1";
        AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(true)
            .permitOriginatedData(PermitOriginatedData.builder()
                .permitType(PermitType.GHGE)
                .installationCategory(InstallationCategory.A_LOW_EMITTER)
                .build())
            .verificationReport(AerVerificationReport.builder()
                .verificationData(AerVerificationData.builder()
                    .uncorrectedMisstatements(UncorrectedMisstatements.builder()
                        .areThereUncorrectedMisstatements(uncorrectedMisstatements)
                        .build())
                    .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                        .areThereUncorrectedNonConformities(uncorrectNonConformities)
                        .areTherePriorYearIssues(areTherePriorYearIssues)
                        .build())
                    .uncorrectedNonCompliances(UncorrectedNonCompliances.builder()
                        .areThereUncorrectedNonCompliances(areThereUncorrectedNonCompliances)
                        .build())
                    .recommendedImprovements(RecommendedImprovements.builder()
                        .areThereRecommendedImprovements(areThereRecommendedImprovements)
                        .build())
                    .build())
                .build())
            .build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(aerRequestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE)))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(true).build());

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, times(1))
            .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));
        verify(virCreationService, never()).createRequestVir(anyString());
        assertThat(((AerRequestPayload) request.getPayload()).isVirTriggered()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideAerVerificationDataForValidAer")
    void createRequestVir_valid_for_all_categories(
        InstallationCategory category, boolean uncorrectedMisstatements, boolean uncorrectNonConformities,
        boolean areTherePriorYearIssues, boolean areThereUncorrectedNonCompliances,
        boolean areThereRecommendedImprovements) {
        final long accountId = 1L;
        final String requestId = "AEM-1";
        AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(true)
            .permitOriginatedData(PermitOriginatedData.builder()
                .permitType(PermitType.GHGE)
                .installationCategory(category)
                .build())
            .verificationReport(AerVerificationReport.builder()
                .verificationData(AerVerificationData.builder()
                    .uncorrectedMisstatements(UncorrectedMisstatements.builder()
                        .areThereUncorrectedMisstatements(uncorrectedMisstatements)
                        .build())
                    .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                        .areThereUncorrectedNonConformities(uncorrectNonConformities)
                        .areTherePriorYearIssues(areTherePriorYearIssues)
                        .build())
                    .uncorrectedNonCompliances(UncorrectedNonCompliances.builder()
                        .areThereUncorrectedNonCompliances(areThereUncorrectedNonCompliances)
                        .build())
                    .recommendedImprovements(RecommendedImprovements.builder()
                        .areThereRecommendedImprovements(areThereRecommendedImprovements)
                        .build())
                    .build())
                .build())
            .build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .metadata(AerRequestMetadata.builder().year(Year.now()).build())
            .payload(aerRequestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE)))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(true).build());
        when(aerService.existsAerByAccountIdAndYear(accountId, Year.now())).thenReturn(false);

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, times(1))
            .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));
        verify(virCreationService, times(1)).createRequestVir(requestId);
        assertThat(((AerRequestPayload) request.getPayload()).isVirTriggered()).isTrue();
    }

    @Test
    void createRequestVir_not_valid_account() {
        final long accountId = 1L;
        final String requestId = "AEM-1";
        AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
            .virTriggered(false)
            .verificationPerformed(true)
            .permitOriginatedData(PermitOriginatedData.builder()
                .permitType(PermitType.GHGE)
                .installationCategory(InstallationCategory.A)
                .build())
            .verificationReport(AerVerificationReport.builder()
                .verificationData(AerVerificationData.builder()
                    .uncorrectedMisstatements(UncorrectedMisstatements.builder()
                        .areThereUncorrectedMisstatements(true)
                        .uncorrectedMisstatements(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .uncorrectedNonConformities(UncorrectedNonConformities.builder()
                        .areThereUncorrectedNonConformities(true)
                        .uncorrectedNonConformities(Set.of(UncorrectedItem.builder().build()))
                        .areTherePriorYearIssues(true)
                        .priorYearIssues(Set.of(VerifierComment.builder().build()))
                        .build())
                    .uncorrectedNonCompliances(UncorrectedNonCompliances.builder()
                        .areThereUncorrectedNonCompliances(true)
                        .uncorrectedNonCompliances(Set.of(UncorrectedItem.builder().build()))
                        .build())
                    .recommendedImprovements(RecommendedImprovements.builder()
                        .areThereRecommendedImprovements(true)
                        .recommendedImprovements(Set.of(VerifierComment.builder().build()))
                        .build())
                    .build())
                .build())
            .build();
        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(aerRequestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestCreateValidatorService.validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE)))
            .thenReturn(RequestCreateAccountStatusValidationResult.builder().valid(false).build());

        // Invoke
        aerCreateVirService.createRequestVir(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestCreateValidatorService, times(1))
            .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));
        verify(virCreationService, never()).createRequestVir(anyString());
        assertThat(((AerRequestPayload) request.getPayload()).isVirTriggered()).isFalse();
    }

    private static Stream<Arguments> provideAerVerificationDataCatALowEmitterInvalidAer() {
        return Stream.of(
            Arguments.of(false, false, false, false, false),
            Arguments.of(false, false, false, false, true)
        );
    }

    private static Stream<Arguments> provideAerVerificationDataForValidAer() {
        return Stream.of(
            Arguments.of(InstallationCategory.A, false, true, false, false, false),
            Arguments.of(InstallationCategory.A, false, true, true, false, false),
            Arguments.of(InstallationCategory.A, false, true, false, false, true),
            Arguments.of(InstallationCategory.A, false, false, false, false, true),
            Arguments.of(InstallationCategory.A, false, false, true, false, false),
            Arguments.of(InstallationCategory.B, false, true, false, false, false),
            Arguments.of(InstallationCategory.B, false, true, true, false, false),
            Arguments.of(InstallationCategory.B, false, true, false, false, true),
            Arguments.of(InstallationCategory.B, false, false, false, false, true),
            Arguments.of(InstallationCategory.B, false, false, true, false, false),
            Arguments.of(InstallationCategory.C, false, true, false, false, false),
            Arguments.of(InstallationCategory.C, false, true, true, false, false),
            Arguments.of(InstallationCategory.C, false, true, false, false, true),
            Arguments.of(InstallationCategory.C, false, false, false, false, true),
            Arguments.of(InstallationCategory.C, false, false, true, false, false),
            Arguments.of(InstallationCategory.A_LOW_EMITTER, false, true, false, false, true)
        );
    }
}
