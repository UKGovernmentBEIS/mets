package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerVerifierContact;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentification;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.FlightIdentificationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
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

class AviationAerUkEtsMapperTest {

    private final AviationAerUkEtsMapper mapper = Mappers.getMapper(AviationAerUkEtsMapper.class);

    @Test
    void toAviationAerUkEtsContainer() {
        EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(AviationOperatorDetails.builder()
                .operatorName("operatorName")
                .crcoCode("crcoCode")
                .flightIdentification(FlightIdentification.builder().flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION).icaoDesignators("all").build())
                .build())
            .build();
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment");
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationBodyDetails(VerificationBodyDetails.builder().name("vb_name").accreditationReferenceNumber("refNumber").build())
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .verifierContact(AviationAerVerifierContact.builder().name("name").build())
                .build())
            .build();
        AviationAerRequestMetadata requestMetadata = AviationAerRequestMetadata.builder()
            .year(Year.of(2022))
            .build();
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
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

        AviationAerUkEtsContainer aerContainer =
            mapper.toAviationAerUkEtsContainer(requestPayload, scheme, accountInfo, requestMetadata);

        assertEquals(scheme, aerContainer.getScheme());
        assertTrue(aerContainer.getReportingRequired());
        assertNull(aerContainer.getReportingObligationDetails());
        assertEquals(requestMetadata.getYear(), aerContainer.getReportingYear());
        assertEquals(verificationReport, aerContainer.getVerificationReport());
        assertEquals(serviceContactDetails, aerContainer.getServiceContactDetails());
        assertThat(aerContainer.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertNull(aerContainer.getSubmittedEmissions());
        assertNull(aerContainer.getReportableEmissions());

        AviationAerUkEts aerContainerAer = aerContainer.getAer();
        assertNotNull(aerContainerAer);
        assertEquals(crcoCodeNew, aerContainerAer.getOperatorDetails().getCrcoCode());
    }

    @Test
    void toAviationAerUkEtsApplicationCompletedRequestActionPayload() {
        RequestActionPayloadType payloadType = RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_COMPLETED_PAYLOAD;
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(AviationOperatorDetails.builder()
                .operatorName("operatorName")
                .crcoCode("crcoCode")
                .flightIdentification(FlightIdentification.builder().flightIdentificationType(FlightIdentificationType.INTERNATIONAL_CIVIL_AVIATION_ORGANISATION).icaoDesignators("all").build())
                .build())
            .build();
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment");
        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();
        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = Map.of(
            AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewDecision,
            AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewDecision
        );
        Map<UUID, String> reviewAttachments = Map.of(UUID.randomUUID(), "reviewAttachment");
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationBodyDetails(VerificationBodyDetails.builder().name("vb_name").accreditationReferenceNumber("refNumber").build())
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .verifierContact(AviationAerVerifierContact.builder().name("name").build())
                .build())
            .build();
        AviationAerRequestMetadata requestMetadata = AviationAerRequestMetadata.builder()
            .year(Year.of(2022))
            .build();
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
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

        AviationAerUkEtsApplicationCompletedRequestActionPayload requestActionPayload =
            mapper.toAviationAerUkEtsApplicationCompletedRequestActionPayload(requestPayload, payloadType, accountInfo, requestMetadata);

        assertEquals(payloadType, requestActionPayload.getPayloadType());
        assertTrue(requestActionPayload.getReportingRequired());
        assertNull(requestActionPayload.getReportingObligationDetails());
        assertEquals(requestMetadata.getYear(), requestActionPayload.getReportingYear());
        assertEquals(verificationReport, requestActionPayload.getVerificationReport());
        assertEquals(serviceContactDetails, requestActionPayload.getServiceContactDetails());
        assertThat(requestActionPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(aerAttachments);
        assertThat(requestActionPayload.getReviewGroupDecisions()).containsExactlyInAnyOrderEntriesOf(reviewGroupDecisions);
        assertThat(requestActionPayload.getReviewAttachments()).containsExactlyInAnyOrderEntriesOf(reviewAttachments);

        AviationAerUkEts aerContainerAer = requestActionPayload.getAer();
        assertNotNull(aerContainerAer);
        assertEquals(crcoCodeNew, aerContainerAer.getOperatorDetails().getCrcoCode());
    }
}