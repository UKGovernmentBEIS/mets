package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.ActivityLevelReport;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerVerificationReportDataReviewDecisionType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AerReviewValidatorServiceTest {

    @InjectMocks
    private AerReviewValidatorService validator;

    private AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
        .verificationData(AerVerificationData.builder()
            .activityLevelReport(ActivityLevelReport.builder().build())
            .build())
        .build();

    @Test
    void validateCompleted_for_verificationData_with_ActivityLevelReport() {
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2Emissions.builder().build()))
                .build())
            .activityLevelReport(ActivityLevelReport.builder().build())
            .build();
        Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap = new HashMap<>() {{
            put(AerReviewGroup.INSTALLATION_DETAILS, getAcceptedAerReviewDecision());
            put(AerReviewGroup.FUELS_AND_EQUIPMENT, getAcceptedAerReviewDecision());
            put(AerReviewGroup.ADDITIONAL_INFORMATION, getAcceptedAerReviewDecision());
            put(AerReviewGroup.EMISSIONS_SUMMARY, getAcceptedAerReviewDecision());
            put(AerReviewGroup.CALCULATION_CO2, getAcceptedAerReviewDecision());
            put(AerReviewGroup.VERIFIER_DETAILS, getVerificationReviewDecision());
            put(AerReviewGroup.OPINION_STATEMENT, getVerificationReviewDecision());
            put(AerReviewGroup.ETS_COMPLIANCE_RULES, getVerificationReviewDecision());
            put(AerReviewGroup.COMPLIANCE_MONITORING_REPORTING, getVerificationReviewDecision());
            put(AerReviewGroup.OVERALL_DECISION, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_MISSTATEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_CONFORMITIES, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_COMPLIANCES, getVerificationReviewDecision());
            put(AerReviewGroup.RECOMMENDED_IMPROVEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.CLOSE_DATA_GAPS_METHODOLOGIES, getVerificationReviewDecision());
            put(AerReviewGroup.MATERIALITY_LEVEL, getVerificationReviewDecision());
            put(AerReviewGroup.SUMMARY_OF_CONDITIONS, getVerificationReviewDecision());
            put(AerReviewGroup.ACTIVITY_LEVEL_REPORT, getVerificationReviewDecision());
            put(AerReviewGroup.VERIFICATION_ACTIVITY_LEVEL_REPORT, getVerificationReviewDecision());
        }};

        validator.validateCompleted(aer, aerReviewGroupAerReviewDecisionMap, true, aerVerificationReport);
    }

    @Test
    void validateCompleted_for_verificationData_without_ActivityLevelReport() {
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2Emissions.builder().build()))
                .build())
            .build();
        AerVerificationReport aerVerificationReport = AerVerificationReport.builder()
            .verificationData(AerVerificationData.builder()
                .build())
            .build();
        Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap = new HashMap<>() {{
            put(AerReviewGroup.INSTALLATION_DETAILS, getAcceptedAerReviewDecision());
            put(AerReviewGroup.FUELS_AND_EQUIPMENT, getAcceptedAerReviewDecision());
            put(AerReviewGroup.ADDITIONAL_INFORMATION, getAcceptedAerReviewDecision());
            put(AerReviewGroup.EMISSIONS_SUMMARY, getAcceptedAerReviewDecision());
            put(AerReviewGroup.CALCULATION_CO2, getAcceptedAerReviewDecision());
            put(AerReviewGroup.VERIFIER_DETAILS, getVerificationReviewDecision());
            put(AerReviewGroup.OPINION_STATEMENT, getVerificationReviewDecision());
            put(AerReviewGroup.ETS_COMPLIANCE_RULES, getVerificationReviewDecision());
            put(AerReviewGroup.COMPLIANCE_MONITORING_REPORTING, getVerificationReviewDecision());
            put(AerReviewGroup.OVERALL_DECISION, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_MISSTATEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_CONFORMITIES, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_COMPLIANCES, getVerificationReviewDecision());
            put(AerReviewGroup.RECOMMENDED_IMPROVEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.CLOSE_DATA_GAPS_METHODOLOGIES, getVerificationReviewDecision());
            put(AerReviewGroup.MATERIALITY_LEVEL, getVerificationReviewDecision());
            put(AerReviewGroup.SUMMARY_OF_CONDITIONS, getVerificationReviewDecision());
        }};

        // Invoke
        validator.validateCompleted(aer, aerReviewGroupAerReviewDecisionMap, true, aerVerificationReport);
    }

    @Test
    void validateCompleted_no_verification() {
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2Emissions.builder().build()))
                .build())
            .activityLevelReport(ActivityLevelReport.builder().build())
            .build();
        Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap = new HashMap<>() {{
            put(AerReviewGroup.INSTALLATION_DETAILS, getAcceptedAerReviewDecision());
            put(AerReviewGroup.FUELS_AND_EQUIPMENT, getAcceptedAerReviewDecision());
            put(AerReviewGroup.ADDITIONAL_INFORMATION, getAcceptedAerReviewDecision());
            put(AerReviewGroup.EMISSIONS_SUMMARY, getAcceptedAerReviewDecision());
            put(AerReviewGroup.CALCULATION_CO2, getAcceptedAerReviewDecision());
            put(AerReviewGroup.ACTIVITY_LEVEL_REPORT, getAcceptedAerReviewDecision());
        }};

        validator.validateCompleted(aer, aerReviewGroupAerReviewDecisionMap, false, null);
    }

    @Test
    void validateCompleted_missing_verification_group() {
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2Emissions.builder().build()))
                .build())
            .build();
        Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap = new HashMap<>() {{
            put(AerReviewGroup.INSTALLATION_DETAILS, getAcceptedAerReviewDecision());
            put(AerReviewGroup.FUELS_AND_EQUIPMENT, getAcceptedAerReviewDecision());
            put(AerReviewGroup.ADDITIONAL_INFORMATION, getAcceptedAerReviewDecision());
            put(AerReviewGroup.EMISSIONS_SUMMARY, getAcceptedAerReviewDecision());
            put(AerReviewGroup.CALCULATION_CO2, getAcceptedAerReviewDecision());
            put(AerReviewGroup.VERIFIER_DETAILS, getVerificationReviewDecision());
            put(AerReviewGroup.OPINION_STATEMENT, getVerificationReviewDecision());
            put(AerReviewGroup.ETS_COMPLIANCE_RULES, getVerificationReviewDecision());
            put(AerReviewGroup.COMPLIANCE_MONITORING_REPORTING, getVerificationReviewDecision());
            put(AerReviewGroup.OVERALL_DECISION, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_MISSTATEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_CONFORMITIES, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_COMPLIANCES, getVerificationReviewDecision());
            put(AerReviewGroup.RECOMMENDED_IMPROVEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.CLOSE_DATA_GAPS_METHODOLOGIES, getVerificationReviewDecision());
            put(AerReviewGroup.SUMMARY_OF_CONDITIONS, getVerificationReviewDecision());
        }};

        BusinessException ex = assertThrows(BusinessException.class, () ->
            validator.validateCompleted(aer, aerReviewGroupAerReviewDecisionMap, true, aerVerificationReport));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateCompleted_missing_aer_group() {
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2Emissions.builder().build()))
                .build())
            .build();
        Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap = new HashMap<>() {{
            put(AerReviewGroup.INSTALLATION_DETAILS, getAcceptedAerReviewDecision());
            put(AerReviewGroup.FUELS_AND_EQUIPMENT, getAcceptedAerReviewDecision());
            put(AerReviewGroup.ADDITIONAL_INFORMATION, getAcceptedAerReviewDecision());
            put(AerReviewGroup.EMISSIONS_SUMMARY, getAcceptedAerReviewDecision());
            put(AerReviewGroup.VERIFIER_DETAILS, getVerificationReviewDecision());
            put(AerReviewGroup.OPINION_STATEMENT, getVerificationReviewDecision());
            put(AerReviewGroup.ETS_COMPLIANCE_RULES, getVerificationReviewDecision());
            put(AerReviewGroup.COMPLIANCE_MONITORING_REPORTING, getVerificationReviewDecision());
            put(AerReviewGroup.OVERALL_DECISION, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_MISSTATEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_CONFORMITIES, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_COMPLIANCES, getVerificationReviewDecision());
            put(AerReviewGroup.RECOMMENDED_IMPROVEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.CLOSE_DATA_GAPS_METHODOLOGIES, getVerificationReviewDecision());
            put(AerReviewGroup.MATERIALITY_LEVEL, getVerificationReviewDecision());
            put(AerReviewGroup.SUMMARY_OF_CONDITIONS, getVerificationReviewDecision());
        }};

        BusinessException ex = assertThrows(BusinessException.class, () ->
            validator.validateCompleted(aer, aerReviewGroupAerReviewDecisionMap, true, aerVerificationReport));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateCompleted_no_verification_group_should_exist() {
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2Emissions.builder().build()))
                .build())
            .build();
        Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap = new HashMap<>() {{
            put(AerReviewGroup.INSTALLATION_DETAILS, getAcceptedAerReviewDecision());
            put(AerReviewGroup.FUELS_AND_EQUIPMENT, getAcceptedAerReviewDecision());
            put(AerReviewGroup.ADDITIONAL_INFORMATION, getAcceptedAerReviewDecision());
            put(AerReviewGroup.EMISSIONS_SUMMARY, getAcceptedAerReviewDecision());
            put(AerReviewGroup.CALCULATION_CO2, getAcceptedAerReviewDecision());
            put(AerReviewGroup.VERIFIER_DETAILS, getVerificationReviewDecision());
        }};

        BusinessException ex = assertThrows(BusinessException.class, () ->
            validator.validateCompleted(aer, aerReviewGroupAerReviewDecisionMap, false, aerVerificationReport));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateCompleted_no_review_groups() {
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2Emissions.builder().build()))
                .build())
            .build();

        BusinessException ex = assertThrows(BusinessException.class, () ->
            validator.validateCompleted(aer, Map.of(), false, aerVerificationReport));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateCompleted_no_monitoring_approach_emissions() {
        Aer aer = Aer.builder().monitoringApproachEmissions(MonitoringApproachEmissions.builder().build()).build();
        Map<AerReviewGroup, AerReviewDecision> aerReviewGroupAerReviewDecisionMap = new HashMap<>() {{
            put(AerReviewGroup.INSTALLATION_DETAILS, getAcceptedAerReviewDecision());
            put(AerReviewGroup.FUELS_AND_EQUIPMENT, getAcceptedAerReviewDecision());
            put(AerReviewGroup.ADDITIONAL_INFORMATION, getAcceptedAerReviewDecision());
            put(AerReviewGroup.EMISSIONS_SUMMARY, getAcceptedAerReviewDecision());
            put(AerReviewGroup.CALCULATION_CO2, getAcceptedAerReviewDecision());
            put(AerReviewGroup.VERIFIER_DETAILS, getVerificationReviewDecision());
            put(AerReviewGroup.OPINION_STATEMENT, getVerificationReviewDecision());
            put(AerReviewGroup.ETS_COMPLIANCE_RULES, getVerificationReviewDecision());
            put(AerReviewGroup.COMPLIANCE_MONITORING_REPORTING, getVerificationReviewDecision());
            put(AerReviewGroup.OVERALL_DECISION, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_MISSTATEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_CONFORMITIES, getVerificationReviewDecision());
            put(AerReviewGroup.UNCORRECTED_NON_COMPLIANCES, getVerificationReviewDecision());
            put(AerReviewGroup.RECOMMENDED_IMPROVEMENTS, getVerificationReviewDecision());
            put(AerReviewGroup.CLOSE_DATA_GAPS_METHODOLOGIES, getVerificationReviewDecision());
            put(AerReviewGroup.MATERIALITY_LEVEL, getVerificationReviewDecision());
            put(AerReviewGroup.SUMMARY_OF_CONDITIONS, getVerificationReviewDecision());
        }};

        BusinessException ex = assertThrows(BusinessException.class, () ->
            validator.validateCompleted(aer, aerReviewGroupAerReviewDecisionMap, false, aerVerificationReport));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    private AerReviewDecision getVerificationReviewDecision() {
        return AerVerificationReportDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.VERIFICATION_REPORT_DATA)
            .type(AerVerificationReportDataReviewDecisionType.ACCEPTED)
            .build();
    }

    private AerReviewDecision getAcceptedAerReviewDecision() {
        return AerDataReviewDecision.builder()
            .reviewDataType(AerReviewDataType.AER_DATA)
            .type(AerDataReviewDecisionType.ACCEPTED)
            .build();
    }
}
