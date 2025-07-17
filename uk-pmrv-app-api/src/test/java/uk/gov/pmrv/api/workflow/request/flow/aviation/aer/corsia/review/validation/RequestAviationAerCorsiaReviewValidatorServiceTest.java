package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.validation;

import org.junit.jupiter.api.Test;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach.AviationAerCorsiaMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestAviationAerCorsiaReviewValidatorServiceTest {

    private final RequestAviationAerCorsiaReviewValidatorService reviewValidatorService = new RequestAviationAerCorsiaReviewValidatorService();

    @Test
    void validateReviewGroups_no_reporting_obligation_valid() {
        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        AviationAerCorsia aerCorsia = AviationAerCorsia.builder()
            .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                .exist(true)
                .build())
            .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.REPORTING_OBLIGATION_DETAILS, aerDataReviewDecision);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(false)
                .reviewGroupDecisions(reviewGroupDecisions)
                .aer(aerCorsia)
                .build();

        reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload, false);
    }

    @Test
    void validateReviewGroups_no_reporting_obligation_invalid() {
        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        AviationAerCorsia aerCorsia = AviationAerCorsia.builder()
            .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                .exist(true)
                .build())
            .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.REPORTING_OBLIGATION_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewDecision);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(false)
                .reviewGroupDecisions(reviewGroupDecisions)
                .aer(aerCorsia)
                .build();

        BusinessException be = assertThrows(BusinessException.class,
            () -> reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload,
                false));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateReviewGroups_without_verification_report_valid() {
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                .build())
            .build();

        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_PLAN_CHANGES, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_APPROACH, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.AGGREGATED_EMISSIONS_DATA, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.AIRCRAFT_DATA, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.EMISSIONS_REDUCTION_CLAIM, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.TOTAL_EMISSIONS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.DATA_GAPS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.CONFIDENTIALITY, aerDataReviewDecision);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload, false);
    }

    @Test
    void validateReviewGroups_with_verification_report_valid() {
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                .build())
            .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                .exist(true)
                .build())
            .build();
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .build();

        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        AerVerificationReportDataReviewDecision verificationReportDataReviewDecision =
            AerVerificationReportDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
                .type(AerVerificationReportDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_PLAN_CHANGES, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_APPROACH, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.AGGREGATED_EMISSIONS_DATA, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.AIRCRAFT_DATA, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.EMISSIONS_REDUCTION_CLAIM, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.TOTAL_EMISSIONS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.DATA_GAPS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.CONFIDENTIALITY, aerDataReviewDecision);

        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.VERIFIER_DETAILS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.TIME_ALLOCATION_AND_SCOPE, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.VERIFICATION_CRITERIA, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.PROCESS_AND_ANALYSIS_DETAILS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_APPROACH_EMISSIONS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.UNCORRECTED_MISSTATEMENTS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.UNCORRECTED_NON_CONFORMITIES, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.UNCORRECTED_NON_COMPLIANCES, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.RECOMMENDED_IMPROVEMENTS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.ELIGIBLE_FUELS_REDUCTION_CLAIM, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.CONCLUSIONS_DATA_QUALITY, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.VERIFICATION_STATEMENT_CONCLUSIONS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.INDEPENDENT_REVIEW, verificationReportDataReviewDecision);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .reviewGroupDecisions(reviewGroupDecisions)
                .verificationReport(verificationReport)
                .build();

        reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload, true);
    }

    @Test
    void validateReviewGroups_invalid_decision() {
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                .build())
            .build();

        AerDataReviewDecision aerDataReviewAcceptedDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        AerDataReviewDecision aerDataReviewAmendsNeededDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .details(ChangesRequiredDecisionDetails.builder().notes("notes").build())
            .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_PLAN_CHANGES, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_APPROACH, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.AGGREGATED_EMISSIONS_DATA, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.AIRCRAFT_DATA, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.EMISSIONS_REDUCTION_CLAIM, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.TOTAL_EMISSIONS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.DATA_GAPS, aerDataReviewAmendsNeededDecision);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        BusinessException be = assertThrows(BusinessException.class,
            () -> reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload,
                false));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateReviewGroups_invalid_missing_group() {
        AviationAerCorsia aer = AviationAerCorsia.builder()
            .monitoringApproach(AviationAerCorsiaMonitoringApproach.builder()
                .build())
            .build();

        AerDataReviewDecision aerDataReviewAcceptedDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_PLAN_CHANGES, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.MONITORING_APPROACH, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.AGGREGATED_EMISSIONS_DATA, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.AIRCRAFT_DATA, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.EMISSIONS_REDUCTION_CLAIM, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.TOTAL_EMISSIONS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.ADDITIONAL_DOCUMENTS, aerDataReviewAcceptedDecision);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        BusinessException be = assertThrows(BusinessException.class,
            () -> reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload, false));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateAtLeastOneReviewGroupAmendsNeeded_invalid() {
        AviationAerCorsia aer = AviationAerCorsia.builder().build();

        AerDataReviewDecision aerDataReviewAcceptedDecision = AerDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.AER_DATA)
                .type(AerDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewAcceptedDecision);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
                AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                        .reportingRequired(true)
                        .aer(aer)
                        .reviewGroupDecisions(reviewGroupDecisions)
                        .build();

        BusinessException be = assertThrows(BusinessException.class,
                () -> reviewValidatorService.validateAtLeastOneReviewGroupAmendsNeeded(reviewRequestTaskPayload));

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.INVALID_AVIATION_AER_REVIEW);
    }

    @Test
    void validateAtLeastOneReviewGroupAmendsNeeded_valid() {
        AviationAerCorsia aer = AviationAerCorsia.builder().build();

        AerDataReviewDecision aerDataReviewAcceptedDecision = AerDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.AER_DATA)
                .type(AerDataReviewDecisionType.ACCEPTED)
                .details(ReviewDecisionDetails.builder().notes("notes").build())
                .build();

        AerDataReviewDecision aerDataReviewAmendsNeededDecision = AerDataReviewDecision.builder()
                .type(AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                .reviewDataType(AerReviewDataType.AER_DATA)
                .details(ChangesRequiredDecisionDetails.builder().build())
                .build();

        Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerCorsiaReviewGroup.OPERATOR_DETAILS, aerDataReviewAmendsNeededDecision);

        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
                AviationAerCorsiaApplicationReviewRequestTaskPayload.builder()
                        .reportingRequired(true)
                        .aer(aer)
                        .reviewGroupDecisions(reviewGroupDecisions)
                        .build();

        reviewValidatorService.validateAtLeastOneReviewGroupAmendsNeeded(reviewRequestTaskPayload);
    }
}