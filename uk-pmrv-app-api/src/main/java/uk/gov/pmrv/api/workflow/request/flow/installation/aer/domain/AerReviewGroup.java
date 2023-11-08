package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import com.google.common.collect.ImmutableMap;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AerReviewGroup {
    INSTALLATION_DETAILS,
    FUELS_AND_EQUIPMENT,
    ADDITIONAL_INFORMATION,
    EMISSIONS_SUMMARY,

    CALCULATION_CO2,
    MEASUREMENT_CO2,
    FALLBACK,
    MEASUREMENT_N2O,
    CALCULATION_PFC,
    INHERENT_CO2,

    VERIFIER_DETAILS,
    OPINION_STATEMENT,
    ETS_COMPLIANCE_RULES,
    COMPLIANCE_MONITORING_REPORTING,
    OVERALL_DECISION,
    UNCORRECTED_MISSTATEMENTS,
    UNCORRECTED_NON_CONFORMITIES,
    UNCORRECTED_NON_COMPLIANCES,
    RECOMMENDED_IMPROVEMENTS,
    CLOSE_DATA_GAPS_METHODOLOGIES,
    MATERIALITY_LEVEL,
    SUMMARY_OF_CONDITIONS,
    ACTIVITY_LEVEL_REPORT,
    VERIFICATION_ACTIVITY_LEVEL_REPORT;

    private static final ImmutableMap<MonitoringApproachType, AerReviewGroup> monitoringApproachTypeAerReviewGroupMap =
        ImmutableMap.<MonitoringApproachType, AerReviewGroup>builder()
            .put(MonitoringApproachType.CALCULATION_CO2, AerReviewGroup.CALCULATION_CO2)
            .put(MonitoringApproachType.MEASUREMENT_CO2, AerReviewGroup.MEASUREMENT_CO2)
            .put(MonitoringApproachType.FALLBACK, AerReviewGroup.FALLBACK)
            .put(MonitoringApproachType.MEASUREMENT_N2O, AerReviewGroup.MEASUREMENT_N2O)
            .put(MonitoringApproachType.CALCULATION_PFC, AerReviewGroup.CALCULATION_PFC)
            .put(MonitoringApproachType.INHERENT_CO2, AerReviewGroup.INHERENT_CO2)
            .build();

    public static Set<AerReviewGroup> getAerDataStandardReviewGroups() {
        return Set.of(
            AerReviewGroup.INSTALLATION_DETAILS,
            AerReviewGroup.FUELS_AND_EQUIPMENT,
            AerReviewGroup.ADDITIONAL_INFORMATION,
            AerReviewGroup.EMISSIONS_SUMMARY
        );
    }

    public static Set<AerReviewGroup> getVerificationDataReviewGroups(AerVerificationReport aerVerificationReport) {
        Set<AerReviewGroup> aerVerificationReviewGroups = Set.of(
            AerReviewGroup.VERIFIER_DETAILS,
            AerReviewGroup.OPINION_STATEMENT,
            AerReviewGroup.ETS_COMPLIANCE_RULES,
            AerReviewGroup.COMPLIANCE_MONITORING_REPORTING,
            AerReviewGroup.OVERALL_DECISION,
            AerReviewGroup.UNCORRECTED_MISSTATEMENTS,
            AerReviewGroup.UNCORRECTED_NON_CONFORMITIES,
            AerReviewGroup.UNCORRECTED_NON_COMPLIANCES,
            AerReviewGroup.RECOMMENDED_IMPROVEMENTS,
            AerReviewGroup.CLOSE_DATA_GAPS_METHODOLOGIES,
            AerReviewGroup.MATERIALITY_LEVEL,
            AerReviewGroup.SUMMARY_OF_CONDITIONS
        );

        if (activityLevelReportExists(aerVerificationReport)) {
            aerVerificationReviewGroups = Stream.concat(aerVerificationReviewGroups.stream(),
                    Stream.of(AerReviewGroup.VERIFICATION_ACTIVITY_LEVEL_REPORT))
                .collect(Collectors.toSet());
        }
        return aerVerificationReviewGroups;
    }

    private static boolean activityLevelReportExists(AerVerificationReport aerVerificationReport) {
        return aerVerificationReport != null
            && aerVerificationReport.getVerificationData() != null
            && aerVerificationReport.getVerificationData().getActivityLevelReport() != null;
    }

    public static Set<AerReviewGroup> getAerDataReviewGroups(Aer aer) {
        Set<AerReviewGroup> aerMonitoringReviewGroups =
            aer.getMonitoringApproachEmissions().getMonitoringApproachEmissions()
                .keySet().stream()
                .map(monitoringApproachTypeAerReviewGroupMap::get)
                .collect(Collectors.toSet());

        if (aer.getActivityLevelReport() != null) {
            aerMonitoringReviewGroups = Stream.concat(aerMonitoringReviewGroups.stream(),
                    Stream.of(AerReviewGroup.ACTIVITY_LEVEL_REPORT))
                .collect(Collectors.toSet());
        }

        return Stream.of(getAerDataStandardReviewGroups(), aerMonitoringReviewGroups)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    public static AerReviewGroup getAerReviewGroupFromMonitoringApproach(final MonitoringApproachType type) {
        return monitoringApproachTypeAerReviewGroupMap.get(type);
    }
}
