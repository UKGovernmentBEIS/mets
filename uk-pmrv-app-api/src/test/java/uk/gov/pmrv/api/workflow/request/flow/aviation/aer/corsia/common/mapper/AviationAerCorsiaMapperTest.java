package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationTeamLeader;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerifierDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

import java.time.Year;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerCorsiaMapperTest {

    private final AviationAerCorsiaMapper mapper = Mappers.getMapper(AviationAerCorsiaMapper.class);

    @Test
    void toAviationAerCorsiaContainer() {
        EmissionTradingScheme scheme = EmissionTradingScheme.CORSIA;
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .operatorDetails(AviationCorsiaOperatorDetails.builder()
                .operatorName("operatorName")
                .flightIdentification(FlightIdentification.builder().flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION).icaoDesignators("all").build())
                .build())
            .build();
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment");
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationBodyDetails(VerificationBodyDetails.builder().name("vb_name").accreditationReferenceNumber("refNumber").build())
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .verifierDetails(AviationAerCorsiaVerifierDetails.builder()
                    .verificationTeamLeader(AviationAerCorsiaVerificationTeamLeader.builder()
                        .name("name")
                        .build())
                    .build())
                .build())
            .build();
        AviationAerCorsiaRequestMetadata requestMetadata = AviationAerCorsiaRequestMetadata.builder()
            .year(Year.of(2022))
            .build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .reportingRequired(true)
            .aer(aer)
            .aerAttachments(aerAttachments)
            .verificationReport(verificationReport)
            .build();
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("name")
            .roleCode("roleCode")
            .email("email")
            .build();
        String crcoCodeNew = "crcoCodeNew";
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(serviceContactDetails)
            .crcoCode(crcoCodeNew)
            .build();

        AviationAerCorsiaContainer aerContainer =
            mapper.toAviationAerCorsiaContainer(requestPayload, scheme, accountInfo, requestMetadata);

        assertEquals(scheme, aerContainer.getScheme());
        assertTrue(aerContainer.getReportingRequired());
        assertNull(aerContainer.getReportingObligationDetails());
        assertEquals(requestMetadata.getYear(), aerContainer.getReportingYear());
        assertEquals(verificationReport, aerContainer.getVerificationReport());
        assertEquals(serviceContactDetails, aerContainer.getServiceContactDetails());
        assertThat(aerContainer.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertNull(aerContainer.getSubmittedEmissions());
        assertNull(aerContainer.getReportableEmissions());

        AviationAerCorsia aerContainerAer = aerContainer.getAer();
        assertNotNull(aerContainerAer);
    }

    @Test
    void toAviationAerCorsiaApplicationCompletedRequestActionPayload() {
        RequestActionPayloadType payloadType = RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_COMPLETED_PAYLOAD;
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .operatorDetails(AviationCorsiaOperatorDetails.builder()
                .operatorName("operatorName")
                .flightIdentification(FlightIdentification.builder().flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION).icaoDesignators("all").build())
                .build())
            .build();
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment");
        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = Map.of(
            AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewDecision,
            AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewDecision
        );
        Map<UUID, String> reviewAttachments = Map.of(UUID.randomUUID(), "reviewAttachment");
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationBodyDetails(VerificationBodyDetails.builder().name("vb_name").accreditationReferenceNumber("refNumber").build())
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .verifierDetails(
                    AviationAerCorsiaVerifierDetails.builder()
                        .verificationTeamLeader(AviationAerCorsiaVerificationTeamLeader.builder()
                            .name("name")
                            .build())
                        .build()
                )
                .build())
            .build();
        AviationAerCorsiaRequestMetadata requestMetadata = AviationAerCorsiaRequestMetadata.builder()
            .year(Year.of(2022))
            .build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .reportingRequired(true)
            .aer(aer)
            .aerAttachments(aerAttachments)
            .verificationReport(verificationReport)
            .reviewGroupDecisions(reviewGroupDecisions)
            .reviewAttachments(reviewAttachments)
            .build();
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("name")
            .roleCode("roleCode")
            .email("email")
            .build();
        String crcoCodeNew = "crcoCodeNew";
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(serviceContactDetails)
            .crcoCode(crcoCodeNew)
            .build();

        AviationAerCorsiaApplicationCompletedRequestActionPayload requestActionPayload =
            mapper.toAviationAerCorsiaApplicationCompletedRequestActionPayload(requestPayload, payloadType, accountInfo, requestMetadata);

        assertEquals(payloadType, requestActionPayload.getPayloadType());
        assertTrue(requestActionPayload.getReportingRequired());
        assertNull(requestActionPayload.getReportingObligationDetails());
        assertEquals(requestMetadata.getYear(), requestActionPayload.getReportingYear());
        assertEquals(verificationReport, requestActionPayload.getVerificationReport());
        assertEquals(serviceContactDetails, requestActionPayload.getServiceContactDetails());
        assertThat(requestActionPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(requestActionPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
        assertThat(requestActionPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);

        AviationAerCorsia aerContainerAer = requestActionPayload.getAer();
        assertNotNull(aerContainerAer);
    }
}