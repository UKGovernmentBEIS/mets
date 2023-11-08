package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.EtsComplianceRules;
import uk.gov.pmrv.api.reporting.domain.verification.MethodologiesToCloseDataGaps;
import uk.gov.pmrv.api.reporting.domain.verification.NonConformities;
import uk.gov.pmrv.api.reporting.domain.verification.SummaryOfConditions;
import uk.gov.pmrv.api.reporting.domain.verification.VerificationTeamDetails;
import uk.gov.pmrv.api.reporting.domain.verification.VerifiedSatisfactoryOverallAssessment;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSaveApplicationVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.RequestAerApplyVerificationService;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerApplySaveVerificationActionHandlerTest {

    @InjectMocks
    private AerApplySaveVerificationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAerApplyVerificationService requestAerApplyVerificationService;

    @Test
    void process() {
        final long taskId = 1L;
        final PmrvUser user = PmrvUser.builder().build();
        final AerSaveApplicationVerificationRequestTaskActionPayload payload = AerSaveApplicationVerificationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AER_SAVE_APPLICATION_VERIFICATION_PAYLOAD)
                .verificationData(AerVerificationData.builder()
                        .verificationTeamDetails(VerificationTeamDetails.builder()
                                .leadEtsAuditor("leadEtsAuditor")
                                .etsAuditors("etsAuditors")
                                .etsTechnicalExperts("etsTechnicalExperts")
                                .independentReviewer("independentReviewer")
                                .technicalExperts("technicalExperts")
                                .authorisedSignatoryName("authorisedSignatoryName")
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
                                .build()
                        )
                        .summaryOfConditions(SummaryOfConditions.builder()
                                .changesNotIncludedInPermit(Boolean.TRUE)
                                .approvedChangesNotIncluded(Set.of(new VerifierComment("A1", "Explanation 1")))
                                .changesIdentified(Boolean.TRUE)
                                .notReportedChanges(Set.of(new VerifierComment("B1", "Explanation 2")))
                                .build()
                        )
                        .overallAssessment(VerifiedSatisfactoryOverallAssessment.builder().build())
                        .methodologiesToCloseDataGaps(MethodologiesToCloseDataGaps.builder()
                                .dataGapRequired(Boolean.FALSE)
                                .build())
                        .build())
                .build();
        final RequestTask task = RequestTask.builder().id(1L).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        // Invoke
        handler.process(taskId, RequestTaskActionType.AER_SAVE_APPLICATION_VERIFICATION, user, payload);

        // Verify
        verify(requestTaskService, times(1))
                .findTaskById(taskId);
        verify(requestAerApplyVerificationService, times(1))
                .applySaveAction(payload, task);
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.AER_SAVE_APPLICATION_VERIFICATION), handler.getTypes());
    }
}
