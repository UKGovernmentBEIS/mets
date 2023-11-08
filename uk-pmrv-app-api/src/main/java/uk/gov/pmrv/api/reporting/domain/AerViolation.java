package uk.gov.pmrv.api.reporting.domain;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class AerViolation {

    private String sectionName;
    private String message;
    private Object[] data;

    public AerViolation(String sectionName, AerViolation.AerViolationMessage aerViolationMessage) {
        this(sectionName, aerViolationMessage, List.of());
    }

    public AerViolation(String sectionName, AerViolation.AerViolationMessage aerViolationMessage, Object... data) {
        this.sectionName = sectionName;
        this.message = aerViolationMessage.getMessage();
        this.data = data;
    }

    @Getter
    public enum AerViolationMessage {
        INVALID_SOURCE_STREAM("Referenced Source stream is not defined in report source streams"),
        INVALID_EMISSION_SOURCE("Referenced Emission source is not defined in report emission sources"),
        INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON("Invalid reason for not using monitoring tiers applied in permit"),
        PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS_SHOULD_NOT_EXIST("Notifications to justify differences in applied monitoring tiers should not exist"),
        INVALID_PARAMETER_MONITORING_TIER_DIFF_REASON_NOTIFICATIONS("Notifications selected to justify differences in applied monitoring tiers are not valid"),
        TOTAL_SUSTAINABLE_BIOMASS_EMISSION_ARE_MISSING("Total sustainable biomass emissions are missing"),
        CALCULATION_INVALID_PARAMETER_MONITORING_TIER("Mandatory calculation parameter monitoring tiers should exist"),
        CALCULATION_INVALID_BIOMASS_FRACTION_MONITORING_TIER("Invalid biomass fraction monitoring tier"),
        CALCULATION_INVALID_PARAMETER_CALCULATION_METHOD("Invalid parameter calculation method"),
        CALCULATION_INCORRECT_TOTAL_EMISSIONS("Total emissions provided differ from the ones calculated by the system"),
        CALCULATION_INVALID_CALCULATION_PARAMETERS("Calculation parameters provided differ from the inventory ones"),
        CALCULATION_INCORRECT_TOTAL_MATERIAL("Total material calculation result differ from the one calculated by the system"),
        CALCULATION_INCORRECT_ACTIVITY_DATA("Total activity data calculation result in 0 degrees differ from the one calculated by the system"),
        CALCULATION_EMISSION_SOURCES_ARE_MISSING("Calculation Emission sources are missing"),
        CALCULATION_INVALID_TRANSFER_OBJECT("Has Transfer and Transfer object are not mutually valid"),
        MEASUREMENT_INCORRECT_TOTAL_EMISSIONS("Total emissions provided differ from the ones calculated by the system"),
        CALCULATION_PFC_INCORRECT_TOTAL_EMISSIONS("Total emissions provided differ from the ones calculated by the system"),

        NO_VERIFICATION_REPORT_FOUND("No verification report found"),
        VERIFIED_DATA_FOUND("Verification report found"),
        VERIFICATION_INVALID_APPROVED_CHANGE_REFERENCE("Regulator approved changes format is not valid"),
        VERIFICATION_INVALID_NOT_REPORTED_CHANGE_REFERENCE("Verification not Reported changes format is not valid"),
        VERIFICATION_INVALID_REPORTABLE_EMISSION_TYPE("Emission type in reportable emissions is not valid"),
        VERIFICATION_INVALID_UNCORRECTED_MISSTATEMENT_REFERENCE("Uncorrected misstatements reference format is not valid"),
        VERIFICATION_INVALID_RECOMMENDED_IMPROVEMENT_REFERENCE("Recommended Improvement reference format is not valid"),
        VERIFICATION_INVALID_UNCORRECTED_NON_CONFORMITIES_REFERENCE("Uncorrected misstatements reference format is not valid"),
        VERIFICATION_INVALID_PRIOR_YEAR_ISSUE_REFERENCE("Prior year issue reference format is not valid"),
        VERIFICATION_INVALID_UNCORRECTED_NON_COMPLIANCES_REFERENCE("Uncorrected non compliances reference format is not valid"),
        INVALID_EMISSION_POINT("Referenced Emission point is not defined in report emission points"),
        ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_HSE("Activity level report task is not applicable for HSE permit type"),
        ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_GHGE("Activity Level Report Task and GHGE permit type are not mutually valid"),
        VERIFICATION_ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_HSE("Verification Activity level report task is not applicable for HSE permit type"),
        VERIFICATION_ACTIVITY_LEVEL_REPORT_NOT_APPLICABLE_GHGE("Verification Activity Level Report Task and GHGE permit type are not mutually valid"),
        ;

        private final String message;

        AerViolationMessage(String message) {
            this.message = message;
        }
    }
}
