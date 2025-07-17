package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.reporting.domain.verification.AccreditationReferenceDocumentType;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.MethodologiesToCloseDataGaps;
import uk.gov.pmrv.api.reporting.domain.verification.SummaryOfConditions;
import uk.gov.pmrv.api.reporting.domain.verification.ComplianceMonitoringReporting;
import uk.gov.pmrv.api.reporting.domain.verification.EtsComplianceRules;
import uk.gov.pmrv.api.reporting.domain.verification.MaterialityLevel;
import uk.gov.pmrv.api.reporting.domain.verification.NonConformities;
import uk.gov.pmrv.api.reporting.domain.verification.VerificationTeamDetails;
import uk.gov.pmrv.api.reporting.domain.verification.VerifiedSatisfactoryOverallAssessment;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.pmrv.api.reporting.domain.verification.VerifierContact;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationVerificationRequestTaskActionPayload;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RequestAerApplyVerificationServiceTest {

    @InjectMocks
    private RequestAerApplyVerificationService service;

    @Test
    void applySaveAction() {

         final AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
                .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD).build();

        final Long accountId = 100L;
        final String requestId = "requestId";
        final Long verificationBodyId = 101L;
        final UUID attachmentId = UUID.randomUUID();

        final Request request = Request.builder()
                    .id(requestId)
                    .accountId(accountId)
                    .payload(aerRequestPayload)
                    .verificationBodyId(verificationBodyId)
                    .build();

        final AerVerificationData verificationData = AerVerificationData.builder()
                .verificationTeamDetails(VerificationTeamDetails.builder()
                        .leadEtsAuditor("leadEtsAuditor")
                        .etsAuditors("etsAuditors")
                        .etsTechnicalExperts("etsTechnicalExperts")
                        .independentReviewer("independentReviewer")
                        .technicalExperts("technicalExperts")
                        .authorisedSignatoryName("authorisedSignatoryName")
                        .build())
                .verifierContact(VerifierContact.builder()
                        .name("name")
                        .email("test@test.gr")
                        .phoneNumber("1234567890")
                        .build())
                .materialityLevel(MaterialityLevel.builder()
                        .materialityDetails("materialityDetails")
                        .accreditationReferenceDocumentTypes(List.of(AccreditationReferenceDocumentType.EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_EA_6_03))
                        .build())
                .complianceMonitoringReporting(ComplianceMonitoringReporting.builder()
                        .accuracy(true)
                        .completeness(true)
                        .consistency(true)
                        .comparability(true)
                        .transparency(true)
                        .integrity(true)
                        .build())
                .etsComplianceRules(EtsComplianceRules.builder()
                        .monitoringPlanRequirementsMet(true)
                        .euRegulationMonitoringReportingMet(true)
                        .detailSourceDataVerified(true)
                        .partOfSiteVerification("Part of site verification")
                        .controlActivitiesDocumented(true)
                        .proceduresMonitoringPlanDocumented(true)
                        .dataVerificationCompleted(true)
                        .monitoringApproachAppliedCorrectly(true)
                        .plannedActualChangesReported(true)
                        .methodsApplyingMissingDataUsed(true)
                        .uncertaintyAssessment(true)
                        .competentAuthorityGuidanceMet(true)
                        .nonConformities(NonConformities.NO)
                        .build())
                .summaryOfConditions(SummaryOfConditions.builder()
                        .changesNotIncludedInPermit(Boolean.TRUE)
                        .approvedChangesNotIncluded(Set.of(new VerifierComment("A1", "Explanation 1")))
                        .changesIdentified(Boolean.TRUE)
                        .notReportedChanges(Set.of(new VerifierComment("B1", "Explanation 2")))
                        .build())
                .overallAssessment(VerifiedSatisfactoryOverallAssessment.builder().build())
                .methodologiesToCloseDataGaps(MethodologiesToCloseDataGaps.builder()
                        .dataGapRequired(Boolean.FALSE)
                        .build())
                .build();
        final AerSaveApplicationVerificationRequestTaskActionPayload actionPayload =
                AerSaveApplicationVerificationRequestTaskActionPayload.builder()
                        .verificationData(verificationData)
                        .verificationSectionsCompleted(Map.of("group", List.of(true)))
                        .build();
        AerApplicationVerificationSubmitRequestTaskPayload taskPayload =
                AerApplicationVerificationSubmitRequestTaskPayload.builder()
                        .payloadType(RequestTaskPayloadType.AER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD)
                        .verificationAttachments(Map.of(attachmentId,"attachment"))
                        .verificationReport(AerVerificationReport.builder()
                                .verificationBodyDetails(VerificationBodyDetails.builder()
                                        .name("nameNew")
                                        .accreditationReferenceNumber("accreditationRefNumNew")
                                        .address(AddressDTO.builder().city("cityNew").country("countryNew").line1("lineNew").build())
                                        .emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS, EmissionTradingScheme.CORSIA))
                                        .build())
                                .build())
                        .build();

        RequestTask requestTask = RequestTask.builder().request(request).payload(taskPayload).build();

        // Invoke
        service.applySaveAction(actionPayload, requestTask);

        // Verify
        AerApplicationVerificationSubmitRequestTaskPayload payloadSaved =
                (AerApplicationVerificationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getVerificationReport().getVerificationData())
                .isEqualTo(verificationData);
        assertThat(payloadSaved.getVerificationSectionsCompleted())
                .isEqualTo(actionPayload.getVerificationSectionsCompleted());
        assertThat(payloadSaved.getVerificationReport().getVerificationBodyDetails())
                .isEqualTo(taskPayload.getVerificationReport().getVerificationBodyDetails());

        assertThat(((AerRequestPayload) request.getPayload()).getVerificationReport())
                    .isEqualTo(taskPayload.getVerificationReport());

        assertThat(((AerRequestPayload) request.getPayload()).isVerificationPerformed()).isFalse();

        assertThat(((AerRequestPayload) request.getPayload()).getVerificationReport().getVerificationBodyId())
                    .isEqualTo(verificationBodyId);

        assertThat(((AerRequestPayload) request.getPayload()).getVerificationSectionsCompleted())
                    .containsExactlyEntriesOf(actionPayload.getVerificationSectionsCompleted());

        assertThat(((AerRequestPayload) request.getPayload()).getVerificationAttachments())
                    .containsExactlyEntriesOf(taskPayload.getVerificationAttachments());
    }
}
