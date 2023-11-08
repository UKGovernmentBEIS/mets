package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.SummaryOfConditions;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;

import java.time.Year;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerApplicationWaitForAmendsInitializerTest {

    @InjectMocks
    private AerApplicationWaitForAmendsInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestVerificationService<AerVerificationReport> requestVerificationService;

    @Test
    void initializePayload() {
        AerVerificationReport verificationReport = AerVerificationReport.builder()
            .verificationData(AerVerificationData.builder()
                .summaryOfConditions(SummaryOfConditions.builder()
                    .build())
                .build())
            .build();
        AerRequestPayload payload = AerRequestPayload.builder()
            .permitOriginatedData(PermitOriginatedData.builder().build())
            .aerAttachments(Map.of(UUID.randomUUID(), "test"))
            .reviewAttachments(Map.of(UUID.randomUUID(), "ReviewAttachment"))
            .aer(Aer.builder().additionalDocuments(AdditionalDocuments.builder().build()).build())
            .verifiedAer(Aer.builder().abbreviations(Abbreviations.builder().build()).build())
            .verificationReport(verificationReport)
            .reviewGroupDecisions(Map.of(AerReviewGroup.FALLBACK,
                AerDataReviewDecision.builder()
                    .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .reviewDataType(AerReviewDataType.AER_DATA).details(ChangesRequiredDecisionDetails.builder().build())
                    .build())
            )
            .build();

        Request request = Request.builder()
            .payload(payload)
            .accountId(1L)
            .metadata(AerRequestMetadata.builder()
                .year(Year.now())
                .build())
            .verificationBodyId(2L)
            .build();
        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().build();
        VerificationBodyDetails verificationBodyDetails = VerificationBodyDetails.builder()
            .name("name")
            .accreditationReferenceNumber("refNumber")
            .emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS))
            .build();


        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
            .thenReturn(installationOperatorDetails);
        when(requestVerificationService.getVerificationBodyDetails(verificationReport, request.getVerificationBodyId()))
            .thenReturn(verificationBodyDetails);

        AerApplicationReviewRequestTaskPayload result =
            (AerApplicationReviewRequestTaskPayload) initializer.initializePayload(request);

        assertEquals(installationOperatorDetails, result
            .getInstallationOperatorDetails());
        assertEquals(payload.getAer(), result
            .getAer());
        assertEquals(payload.getVerifiedAer(), result
            .getVerifiedAer());
        assertEquals(payload.getReviewAttachments(), result
            .getReviewAttachments());
        assertEquals(payload.getAerAttachments(), result
            .getAerAttachments());
        assertThat(payload.getReviewGroupDecisions())
            .containsExactlyEntriesOf(result.getReviewGroupDecisions());
        assertEquals(payload.getVerificationReport(), result.getVerificationReport());
    }

    @Test
    void getRequestTaskTypes() {
        Set<RequestTaskType> result = initializer.getRequestTaskTypes();
        assertEquals(Collections.singleton(RequestTaskType.AER_WAIT_FOR_AMENDS), result);
    }


}