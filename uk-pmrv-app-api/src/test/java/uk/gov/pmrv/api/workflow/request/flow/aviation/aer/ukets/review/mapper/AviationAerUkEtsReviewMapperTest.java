package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGaps;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerFuelMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerificationDecisionType;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerifiedSatisfactoryDecision;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerVerifierContact;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
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

class AviationAerUkEtsReviewMapperTest {

    private final AviationAerUkEtsReviewMapper reviewMapper = Mappers.getMapper(AviationAerUkEtsReviewMapper.class);

    @Test
    void toAviationAerUkEtsApplicationReviewRequestTaskPayload() {
        AviationOperatorDetails operatorDetails = AviationOperatorDetails.builder()
            .operatorName("name")
            .crcoCode("code")
            .build();
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(operatorDetails)
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
            .saf(AviationAerSaf.builder().exist(Boolean.FALSE).build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of(
            "operatorDetails", List.of(true),
            "monitoringApproach", List.of(true),
            "saf", List.of(true));
        Map<UUID, String> aerAttachments = Map.of(UUID.randomUUID(), "attachment1");
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .verifierContact(AviationAerVerifierContact.builder().name("name").email("email").phoneNumber("3345678945").build())
                .overallDecision(AviationAerVerifiedSatisfactoryDecision.builder().type(AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY).build())
                .build())
            .build();
        Map<String, List<Boolean>> verificationSectionsCompleted = Map.of(
            "verifierContact", List.of(true),
            "overallDecision", List.of(true));
        BigDecimal totalEmissionsProvided = BigDecimal.valueOf(14500);
        String notCoveredChangesProvided = "not covered changes";
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
            .reportingRequired(Boolean.TRUE)
            .aer(aer)
            .aerSectionsCompleted(aerSectionsCompleted)
            .aerAttachments(aerAttachments)
            .verificationReport(verificationReport)
            .verificationSectionsCompleted(verificationSectionsCompleted)
            .totalEmissionsProvided(totalEmissionsProvided)
            .notCoveredChangesProvided(notCoveredChangesProvided)
            .build();
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().name("contactName").email("contactEmail").build();
        String updatedCrcoCode = "updated_code";
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(serviceContactDetails)
            .crcoCode(updatedCrcoCode)
            .build();
        RequestTaskPayloadType payloadType = RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD;
        Year reportingYear = Year.of(2023);

        AviationAerUkEtsApplicationReviewRequestTaskPayload expected = AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(payloadType)
            .reportingYear(reportingYear)
            .serviceContactDetails(serviceContactDetails)
            .reportingRequired(true)
            .aerSectionsCompleted(aerSectionsCompleted)
            .aerAttachments(aerAttachments)
            .verificationReport(verificationReport)
            .verificationSectionsCompleted(verificationSectionsCompleted)
            .aer(AviationAerUkEts.builder()
                .operatorDetails(AviationOperatorDetails.builder()
                    .operatorName("name")
                    .crcoCode(updatedCrcoCode)
                    .build())
                .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
                .saf(AviationAerSaf.builder().exist(Boolean.FALSE)
                    .build())
                .build())
            .totalEmissionsProvided(totalEmissionsProvided)
            .notCoveredChangesProvided(notCoveredChangesProvided)
            .build();

        //invoke
        AviationAerUkEtsApplicationReviewRequestTaskPayload result =
            reviewMapper.toAviationAerUkEtsApplicationReviewRequestTaskPayload(requestPayload, accountInfo, payloadType, reportingYear);

        //verify
        assertEquals(expected, result);
    }

    @Test
    void toAviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload() {

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

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewAmendsNeededDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.VERIFIER_DETAILS, verificationReportDataReviewDecision);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload = AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_REVIEW_PAYLOAD)
            .reportingRequired(true)
            .aer(AviationAerUkEts.builder().build())
            .reviewGroupDecisions(reviewGroupDecisions)
            .reviewAttachments(reviewAttachments)
            .build();

        AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload expectedRequestActionPayload =
            AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                .reviewAttachments(reviewAttachments)
                .reviewGroupDecisions(Map.of(
                    AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, AerDataReviewDecision.builder()
                        .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                        .reviewDataType(AerReviewDataType.AER_DATA)
                        .details(ChangesRequiredDecisionDetails.builder()
                            .requiredChanges(reviewDecisionRequiredChanges)
                            .build())
                        .build()
                ))
                .build();

        //invoke
        AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload resultRequestActionPayload =
            reviewMapper.toAviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload(
                reviewRequestTaskPayload,
                RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
            );

        //verify
        assertEquals(expectedRequestActionPayload, resultRequestActionPayload);
    }

    @Test
    void toAviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload() {
        AviationOperatorDetails operatorDetails = AviationOperatorDetails.builder()
            .operatorName("name")
            .crcoCode("code")
            .build();
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(operatorDetails)
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
            .saf(AviationAerSaf.builder().exist(Boolean.FALSE).build())
            .build();
        Map<String, List<Boolean>> aerSectionsCompleted = Map.of(
            "operatorDetails", List.of(true),
            "monitoringApproach", List.of(true),
            "saf", List.of(true));
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

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewAmendsNeededDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.VERIFIER_DETAILS, verificationReportDataReviewDecision);

        Map<String, Boolean> reviewSectionsCompleted = Map.of(
            "operatorDetails", true,
            "monitoringApproach", true);

        Map<UUID, String> reviewAttachments = Map.of(reviewDecisionAttachmentId, "reviewAttachment1");

        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationBodyId(20L)
            .build();
        AviationAerUkEtsRequestPayload requestPayload = AviationAerUkEtsRequestPayload.builder()
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
        String updatedCrcoCode = "updated_code";
        RequestAviationAccountInfo accountInfo = RequestAviationAccountInfo.builder()
            .serviceContactDetails(serviceContactDetails)
            .crcoCode(updatedCrcoCode)
            .build();
        RequestTaskPayloadType payloadType = RequestTaskPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD;
        Year reportingYear = Year.of(2023);

        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload expected = AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
            .payloadType(payloadType)
            .reportingYear(reportingYear)
            .serviceContactDetails(serviceContactDetails)
            .reportingRequired(true)
            .aerSectionsCompleted(aerSectionsCompleted)
            .aerAttachments(aerAttachments)
            .aer(AviationAerUkEts.builder()
                .operatorDetails(AviationOperatorDetails.builder()
                    .operatorName("name")
                    .crcoCode(updatedCrcoCode)
                    .build())
                .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
                .saf(AviationAerSaf.builder().exist(Boolean.FALSE)
                    .build())
                .build())
            .reviewGroupDecisions(Map.of(
                AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, AerDataReviewDecision.builder()
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
        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload result =
            reviewMapper.toAviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload(requestPayload, accountInfo, payloadType, reportingYear);

        //verify
        assertEquals(expected, result);
    }

    @Test
    void toAviationAerUkEtsContainer_without_verification_report() {
        EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("name")
            .roleCode("roleCode")
            .email("email@email.uk")
            .build();
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(AviationOperatorDetails.builder().operatorName("name").crcoCode("crco").build())
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
            .dataGaps(AviationAerDataGaps.builder().exist(false).build())
            .build();
        Year reportingYear = Year.of(2022);
        Map<UUID, String> aerAttachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );
        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                .reportingYear(reportingYear)
                .reportingRequired(true)
                .serviceContactDetails(serviceContactDetails)
                .aer(aer)
                .aerAttachments(aerAttachments)
                .build();

        AviationAerUkEtsContainer expected = AviationAerUkEtsContainer.builder()
            .scheme(scheme)
            .reportingYear(reportingYear)
            .reportingRequired(true)
            .serviceContactDetails(serviceContactDetails)
            .aer(aer)
            .aerAttachments(aerAttachments)
            .build();


        //invoke
        AviationAerUkEtsContainer result = reviewMapper.toAviationAerUkEtsContainer(amendsSubmitRequestTaskPayload, scheme);

        //verify
        assertEquals(expected, result);
    }

    @Test
    void toAviationAerUkEtsContainer_with_verification_report() {
        EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("name")
            .roleCode("roleCode")
            .email("email@email.uk")
            .build();
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(AviationOperatorDetails.builder().operatorName("name").crcoCode("crco").build())
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
            .dataGaps(AviationAerDataGaps.builder().exist(false).build())
            .build();
        Year reportingYear = Year.of(2022);
        Map<UUID, String> aerAttachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .safExists(false)
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .verifierContact(AviationAerVerifierContact.builder().name("name").email("email").phoneNumber("3345678945").build())
                .overallDecision(AviationAerVerifiedSatisfactoryDecision.builder().type(AviationAerVerificationDecisionType.VERIFIED_AS_SATISFACTORY).build())
                .build())
            .build();
        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                .reportingYear(reportingYear)
                .reportingRequired(true)
                .serviceContactDetails(serviceContactDetails)
                .aer(aer)
                .aerAttachments(aerAttachments)
                .build();

        AviationAerUkEtsContainer expected = AviationAerUkEtsContainer.builder()
            .scheme(scheme)
            .reportingYear(reportingYear)
            .reportingRequired(true)
            .serviceContactDetails(serviceContactDetails)
            .aer(aer)
            .aerAttachments(aerAttachments)
            .verificationReport(verificationReport)
            .build();


        //invoke
        AviationAerUkEtsContainer result = reviewMapper.toAviationAerUkEtsContainer(amendsSubmitRequestTaskPayload, verificationReport, scheme);

        //verify
        assertEquals(expected, result);
    }


    @Test
    void toAviationAerUkEtsApplicationSubmittedRequestActionPayload() {
        RequestActionPayloadType payloadType = RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD;
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

        AviationAerUkEts aer = AviationAerUkEts.builder()
            .operatorDetails(AviationOperatorDetails.builder().operatorName("name").crcoCode("crco").build())
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
            .dataGaps(AviationAerDataGaps.builder().exist(false).build())
            .build();
        Map<UUID, String> aerAttachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );
        Year reportingYear = Year.of(2022);

        AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload amendsSubmitRequestTaskPayload =
            AviationAerUkEtsApplicationAmendsSubmitRequestTaskPayload.builder()
                .reportingYear(reportingYear)
                .reportingRequired(true)
                .aer(aer)
                .aerAttachments(aerAttachments)
                .build();

        AviationAerUkEtsApplicationSubmittedRequestActionPayload expected =
            AviationAerUkEtsApplicationSubmittedRequestActionPayload.builder()
                .payloadType(payloadType)
                .reportingYear(reportingYear)
                .reportingRequired(true)
                .aer(AviationAerUkEts.builder()
                    .operatorDetails(AviationOperatorDetails.builder().operatorName("name").crcoCode(accountCrcoCode).build())
                    .monitoringApproach(AviationAerFuelMonitoringApproach.builder().build())
                    .dataGaps(AviationAerDataGaps.builder().exist(false).build())
                    .build())
                .aerAttachments(aerAttachments)
                .serviceContactDetails(serviceContactDetails)
                .build();

        //invoke
        AviationAerUkEtsApplicationSubmittedRequestActionPayload result =
            reviewMapper.toAviationAerUkEtsApplicationSubmittedRequestActionPayload(amendsSubmitRequestTaskPayload, accountInfo, payloadType);

        //verify
        assertEquals(expected, result);
    }
}