package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerFuelMonitoringApproach;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
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

class RequestAviationAerUkEtsReviewValidatorServiceTest {

    private final RequestAviationAerUkEtsReviewValidatorService reviewValidatorService = new RequestAviationAerUkEtsReviewValidatorService();

    @Test
    void validateReviewGroups_no_reporting_obligation_valid() {
        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.REPORTING_OBLIGATION_DETAILS, aerDataReviewDecision);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(false)
                .reviewGroupDecisions(reviewGroupDecisions)
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

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.REPORTING_OBLIGATION_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewDecision);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(false)
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        BusinessException be = assertThrows(BusinessException.class,
            () -> reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload, false));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateReviewGroups_without_verification_report_valid() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                .build())
            .build();

        AerDataReviewDecision aerDataReviewDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_PLAN_CHANGES, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_APPROACH, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.AGGREGATED_EMISSIONS_DATA, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.AIRCRAFT_DATA, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.TOTAL_EMISSIONS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.DATA_GAPS, aerDataReviewDecision);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload, false);
    }

    @Test
    void validateReviewGroups_with_verification_report_valid() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                .build())
            .build();
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .safExists(Boolean.TRUE)
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

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_PLAN_CHANGES, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_APPROACH, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.AGGREGATED_EMISSIONS_DATA, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.AIRCRAFT_DATA, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.TOTAL_EMISSIONS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, aerDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.DATA_GAPS, aerDataReviewDecision);

        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.VERIFIER_DETAILS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OPINION_STATEMENT, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.ETS_COMPLIANCE_RULES, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.COMPLIANCE_MONITORING_REPORTING, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OVERALL_DECISION, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.UNCORRECTED_MISSTATEMENTS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.UNCORRECTED_NON_CONFORMITIES, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.UNCORRECTED_NON_COMPLIANCES, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.RECOMMENDED_IMPROVEMENTS, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM_VERIFICATION, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.CLOSE_DATA_GAPS_METHODOLOGIES, verificationReportDataReviewDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MATERIALITY_LEVEL, verificationReportDataReviewDecision);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .reviewGroupDecisions(reviewGroupDecisions)
                .verificationReport(verificationReport)
                .build();

        reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload, true);
    }

    @Test
    void validateReviewGroups_invalid_decision() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
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

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_PLAN_CHANGES, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_APPROACH, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.AGGREGATED_EMISSIONS_DATA, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.AIRCRAFT_DATA, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.TOTAL_EMISSIONS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.DATA_GAPS, aerDataReviewAmendsNeededDecision);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        BusinessException be = assertThrows(BusinessException.class,
            () -> reviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload, false));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateReviewGroups_invalid_missing_group() {
        AviationAerUkEts aer = AviationAerUkEts.builder()
            .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                .build())
            .build();

        AerDataReviewDecision aerDataReviewAcceptedDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_PLAN_CHANGES, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.MONITORING_APPROACH, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.AGGREGATED_EMISSIONS_DATA, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.AIRCRAFT_DATA, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.TOTAL_EMISSIONS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.ADDITIONAL_DOCUMENTS, aerDataReviewAcceptedDecision);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
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
        AviationAerUkEts aer = AviationAerUkEts.builder().build();

        AerDataReviewDecision aerDataReviewAcceptedDecision = AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .details(ReviewDecisionDetails.builder().notes("notes").build())
            .build();

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewAcceptedDecision);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
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
        AviationAerUkEts aer = AviationAerUkEts.builder().build();

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

        Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new HashMap<>();
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.SERVICE_CONTACT_DETAILS, aerDataReviewAcceptedDecision);
        reviewGroupDecisions.put(AviationAerUkEtsReviewGroup.OPERATOR_DETAILS, aerDataReviewAmendsNeededDecision);

        AviationAerUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            AviationAerUkEtsApplicationReviewRequestTaskPayload.builder()
                .reportingRequired(true)
                .aer(aer)
                .reviewGroupDecisions(reviewGroupDecisions)
                .build();

        reviewValidatorService.validateAtLeastOneReviewGroupAmendsNeeded(reviewRequestTaskPayload);
    }
}