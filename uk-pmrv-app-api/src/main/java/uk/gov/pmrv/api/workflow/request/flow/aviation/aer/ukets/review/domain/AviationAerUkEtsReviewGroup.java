package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain;

import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

import java.util.HashSet;
import java.util.Set;

public enum AviationAerUkEtsReviewGroup {

    //AER related
    SERVICE_CONTACT_DETAILS,
    OPERATOR_DETAILS,
    MONITORING_PLAN_CHANGES,
    MONITORING_APPROACH,
    AGGREGATED_EMISSIONS_DATA,
    AIRCRAFT_DATA,
    EMISSIONS_REDUCTION_CLAIM,
    TOTAL_EMISSIONS,
    ADDITIONAL_DOCUMENTS,

    DATA_GAPS,
    REPORTING_OBLIGATION_DETAILS,

    //verification report related
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

    EMISSIONS_REDUCTION_CLAIM_VERIFICATION,
    ;

    public static Set<AviationAerUkEtsReviewGroup> getVerificationReportDataReviewGroups(AviationAerUkEtsVerificationReport verificationReport) {
        Set<AviationAerUkEtsReviewGroup> verificationReportReviewGroups = new HashSet<>(Set.of(
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
            MATERIALITY_LEVEL
        ));

        if(Boolean.TRUE.equals(verificationReport.getSafExists())) {
            verificationReportReviewGroups.add(EMISSIONS_REDUCTION_CLAIM_VERIFICATION);
        }

        return verificationReportReviewGroups;
    }

    public static Set<AviationAerUkEtsReviewGroup> getAerDataReviewGroups(AviationAerUkEts aer, boolean isReportingRequired) {
        Set<AviationAerUkEtsReviewGroup> aerReviewGroups = new HashSet<>();

        if(isReportingRequired) {
            aerReviewGroups.add(SERVICE_CONTACT_DETAILS);
            aerReviewGroups.add(OPERATOR_DETAILS);
            aerReviewGroups.add(MONITORING_PLAN_CHANGES);
            aerReviewGroups.add(MONITORING_APPROACH);
            aerReviewGroups.add(AGGREGATED_EMISSIONS_DATA);
            aerReviewGroups.add(AIRCRAFT_DATA);
            aerReviewGroups.add(EMISSIONS_REDUCTION_CLAIM);
            aerReviewGroups.add(TOTAL_EMISSIONS);
            aerReviewGroups.add(ADDITIONAL_DOCUMENTS);

            if(EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(aer.getMonitoringApproach().getMonitoringApproachType())) {
                aerReviewGroups.add(DATA_GAPS);
            }
        } else {
            aerReviewGroups.add(REPORTING_OBLIGATION_DETAILS);
        }

        return aerReviewGroups;
    }
}
