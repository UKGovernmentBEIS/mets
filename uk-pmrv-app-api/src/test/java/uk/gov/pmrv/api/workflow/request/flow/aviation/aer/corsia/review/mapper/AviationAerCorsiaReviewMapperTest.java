package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerificationDecisionType;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerifiedSatisfactoryDecision;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps.AviationAerCorsiaDataGaps;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationTeamLeader;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerifierDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.AviationCorsiaOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AviationAerCorsiaReviewMapperTest {

    private final AviationAerCorsiaReviewMapper reviewMapper = Mappers.getMapper(AviationAerCorsiaReviewMapper.class);

    @Test
    void toAviationAerCorsiaApplicationReviewRequestTaskPayload() {
        AviationCorsiaOperatorDetails operatorDetails = AviationCorsiaOperatorDetails.builder()
            .operatorName("name")
            .build();
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .operatorDetails(operatorDetails)
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of(
            "operatorDetails", List.of(true),
            "monitoringApproach", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment1");
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .verifierDetails(AviationAerCorsiaVerifierDetails.builder()
                    .verificationTeamLeader(AviationAerCorsiaVerificationTeamLeader.builder()
                        .name("name")
                        .email("email")
                        .build())
                    .build())
                .overallDecision(AviationAerVerifiedSatisfactoryDecision.builder().type(AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY).build())
                .build())
            .build();
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of(
            "verifierContact", List.of(true),
            "overallDecision", List.of(true));
        BigDecimal totalEmissionsProvided = BigDecimal.valueOf(14500);
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
            .reportingRequired(Boolean.TRUE)
            .aer(aer)
            .aerSectionsCompleted(aerSectionsCompleted)
            .aerAttachments(aerAttachments)
            .verificationReport(verificationReport)
            .verificationSectionsCompleted(verificationSectionsCompleted)
            .totalEmissionsProvided(totalEmissionsProvided)
            .build();
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("contactName").email("contactEmail").build();
        String updatedCrcoCode = "updated_code";
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(serviceContactDetails)
            .crcoCode(updatedCrcoCode)
            .build();
        RequestTaskPayloadType payloadType = RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_REVIEW_PAYLOAD;
        Year reportingYear = Year.of(2023);

        AviationAerCorsiaApplicationReviewRequestTaskPayload expected = AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
            .payloadType(payloadType)
            .reportingYear(reportingYear)
            .serviceContactDetails(serviceContactDetails)
            .reportingRequired(true)
            .aerSectionsCompleted(aerSectionsCompleted)
            .aerAttachments(aerAttachments)
            .verificationReport(verificationReport)
            .verificationSectionsCompleted(verificationSectionsCompleted)
            .aer(AviationAerCorsia.builder()
                .operatorDetails(AviationCorsiaOperatorDetails.builder()
                    .operatorName("name")
                    .build())
                .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().build())
                .build())
            .totalEmissionsProvided(totalEmissionsProvided)
            .build();

        //invoke
        AviationAerCorsiaApplicationReviewRequestTaskPayload result =
            reviewMapper.toAviationAerCorsiaApplicationReviewRequestTaskPayload(requestPayload, accountInfo, payloadType, reportingYear);

        //verify
        assertEquals(expected, result);
    }

    @Test
    void toAviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload() {

        Map<UUID, String> reviewAttachments = Map.of(
                UUID.randomUUID(), "attachment1",
                UUID.randomUUID(), "attachment2"
        );

        AerDataReviewDecision aerDataReviewAcceptedDecision = AerDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.AER_DATA)
                .type(AerDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();

        List<ReviewDecisionRequiredChange> reviewDecisionRequiredChanges = List.of(
                ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(UUID.randomUUID())).build(),
                ReviewDecisionRequiredChange.builder().reason("another reason").build()
        );

        AerDataReviewDecision aerDataReviewAmendsNeededDecision = AerDataReviewDecision.builder()
                .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .reviewDataType(AerReviewDataType.AER_DATA)
                .details(ChangesRequiredDecisionDetails.builder()
                        .notes("notes")
                        .requiredChanges(reviewDecisionRequiredChanges)
                        .build())
                .build();

        AerVerificationReportDataReviewDecision verificationReportDataReviewDecision =
                AerVerificationReportDataReviewDecision.builder()
                        .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
                        .type(AerVerificationReportDataReviewDecisionType.ACCEPTED)
                        .details(ReviewDecisionDetails.builder().notes("notes").build())
                        .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewAmendsNeededDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.VERIFIER_DETAILS, verificationReportDataReviewDecision);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload = AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD)
                .reportingRequired(true)
                .aer(AviationAerCorsia.builder().build())
                .reviewGroupDecisions(reviewGroupDecisions)
                .reviewAttachments(reviewAttachments)
                .build();

        AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload expectedRequestActionPayload =
                AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                        .reviewAttachments(reviewAttachments)
                        .reviewGroupDecisions(Map.of(
                                AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, AerDataReviewDecision.builder()
                                        .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                                        .reviewDataType(AerReviewDataType.AER_DATA)
                                        .details(ChangesRequiredDecisionDetails.builder()
                                                .requiredChanges(reviewDecisionRequiredChanges)
                                                .build())
                                        .build()
                        ))
                        .build();

        //invoke
        AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload resultRequestActionPayload =
                reviewMapper.toAviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload(
                        reviewRequestTaskPayload,
                        RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
                );

        //verify
        assertEquals(expectedRequestActionPayload, resultRequestActionPayload);
    }

    @Test
    void toAviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload() {
        AviationCorsiaOperatorDetails operatorDetails = AviationCorsiaOperatorDetails.builder()
                .operatorName("name")
                .build();
        AviationAerCorsia aer = AviationAerCorsia.builder()
                .operatorDetails(operatorDetails)
                .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().build())
                .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of(
                "operatorDetails", List.of(true),
                "monitoringApproach", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment1");

        AerDataReviewDecision aerDataReviewAcceptedDecision = AerDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.AER_DATA)
                .type(AerDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();

        UUID reviewDecisionAttachmentId = UUID.randomUUID();
        List<ReviewDecisionRequiredChange> reviewDecisionRequiredChanges = List.of(
                ReviewDecisionRequiredChange.builder().reason("reason").files(Set.of(reviewDecisionAttachmentId)).build(),
                ReviewDecisionRequiredChange.builder().reason("another reason").build()
        );
        AerDataReviewDecision aerDataReviewAmendsNeededDecision = AerDataReviewDecision.builder()
                .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .reviewDataType(AerReviewDataType.AER_DATA)
                .details(ChangesRequiredDecisionDetails.builder()
                        .notes("notes")
                        .requiredChanges(reviewDecisionRequiredChanges)
                        .build())
                .build();

        AerVerificationReportDataReviewDecision verificationReportDataReviewDecision =
                AerVerificationReportDataReviewDecision.builder()
                        .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
                        .type(AerVerificationReportDataReviewDecisionType.ACCEPTED)
                        .details(ReviewDecisionDetails.builder().notes("notes").build())
                        .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewAmendsNeededDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.VERIFIER_DETAILS, verificationReportDataReviewDecision);

        Map<String, Boolean> reviewSectionsCompleted = Map.of(
                "operatorDetails", true,
                "monitoringApproach", true);

        Map<UUID, String> reviewAttachments = Map.of(reviewDecisionAttachmentId, "reviewAttachment1");

        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
                .verificationBodyId(20L)
                .build();
        AviationAerCorsiaRequestPayload requestPayload = AviationAerCorsiaRequestPayload.builder()
                .reportingRequired(Boolean.TRUE)
                .aer(aer)
                .aerSectionsCompleted(aerSectionsCompleted)
                .aerAttachments(aerAttachments)
                .verificationPerformed(true)
                .verificationReport(verificationReport)
                .reviewGroupDecisions(reviewGroupDecisions)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .reviewAttachments(reviewAttachments)
                .build();
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("contactName").email("contactEmail").build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
                .serviceContactDetails(serviceContactDetails)
                .build();
        RequestTaskPayloadType payloadType = RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD;
        Year reportingYear = Year.of(2023);

        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload expected = AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                .payloadType(payloadType)
                .reportingYear(reportingYear)
                .serviceContactDetails(serviceContactDetails)
                .reportingRequired(true)
                .aerSectionsCompleted(aerSectionsCompleted)
                .aerAttachments(aerAttachments)
                .aer(AviationAerCorsia.builder()
                        .operatorDetails(AviationCorsiaOperatorDetails.builder()
                                .operatorName("name")
                                .build())
                        .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().build())
                        .build())
                .reviewGroupDecisions(Map.of(
                        AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, AerDataReviewDecision.builder()
                                .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                                .reviewDataType(AerReviewDataType.AER_DATA)
                                .details(ChangesRequiredDecisionDetails.builder()
                                        .requiredChanges(reviewDecisionRequiredChanges)
                                        .build())
                                .build()
                ))
                .reviewAttachments(reviewAttachments)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .verificationPerformed(true)
                .verificationBodyId(20L)
                .build();

        //invoke
        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload result =
                reviewMapper.toAviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload(requestPayload, accountInfo, payloadType, reportingYear);

        //verify
        assertEquals(expected, result);
    }

    @Test
    void toAviationAerCorsiaContainer_without_verification_report() {
        EmissionTradingScheme scheme = EmissionTradingScheme.CORSIA;
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("name")
            .roleCode("roleCode")
            .email("email@email.uk")
            .build();
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .operatorDetails(AviationCorsiaOperatorDetails.builder().operatorName("name").build())
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().build())
            .dataGaps(AviationAerCorsiaDataGaps.builder().exist(false).build())
            .build();
        Year reportingYear = Year.of(2022);
        Map<UUID, String> aerAttachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );
        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
            AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                .reportingYear(reportingYear)
                .reportingRequired(true)
                .serviceContactDetails(serviceContactDetails)
                .aer(aer)
                .aerAttachments(aerAttachments)
                .build();

        AviationAerCorsiaContainer expected = AviationAerCorsiaContainer.builder()
            .scheme(scheme)
            .reportingYear(reportingYear)
            .reportingRequired(true)
            .serviceContactDetails(serviceContactDetails)
            .aer(aer)
            .aerAttachments(aerAttachments)
            .build();

        AviationAerCorsiaContainer result = reviewMapper.toAviationAerCorsiaContainer(amendsSubmitRequestTaskPayload, scheme);

        assertEquals(expected, result);
    }

    @Test
    void toAviationAerCorsiaApplicationSubmittedRequestActionPayload() {
        RequestActionPayloadType payloadType = RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD;
        String accountOperatorName = "accountOperatorName";
        String accountCrcoCode = "accountCrcoCode";
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("name")
            .roleCode("roleCode")
            .email("email@email.uk")
            .build();
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .operatorName(accountOperatorName)
            .crcoCode(accountCrcoCode)
            .serviceContactDetails(serviceContactDetails)
            .build();

        AviationAerCorsia aer = AviationAerCorsia.builder()
            .operatorDetails(AviationCorsiaOperatorDetails.builder().operatorName("name").build())
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().build())
            .dataGaps(AviationAerCorsiaDataGaps.builder().exist(false).build())
            .build();
        Map<UUID, String> aerAttachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );
        Year reportingYear = Year.of(2022);

        AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
            AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload.builder()
                .reportingYear(reportingYear)
                .reportingRequired(true)
                .aer(aer)
                .aerAttachments(aerAttachments)
                .build();

        AviationAerCorsiaApplicationSubmittedRequestActionPayload expected =
            AviationAerCorsiaApplicationSubmittedRequestActionPayload.builder()
                .payloadType(payloadType)
                .reportingYear(reportingYear)
                .reportingRequired(true)
                .aer(AviationAerCorsia.builder()
                    .operatorDetails(AviationCorsiaOperatorDetails.builder().operatorName("name").build())
                    .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder().build())
                    .dataGaps(AviationAerCorsiaDataGaps.builder().exist(false).build())
                    .build())
                .aerAttachments(aerAttachments)
                .serviceContactDetails(serviceContactDetails)
                .build();

        AviationAerCorsiaApplicationSubmittedRequestActionPayload result =
            reviewMapper.toAviationAerCorsiaApplicationSubmittedRequestActionPayload(amendsSubmitRequestTaskPayload, accountInfo, payloadType);

        assertEquals(expected, result);
    }
}