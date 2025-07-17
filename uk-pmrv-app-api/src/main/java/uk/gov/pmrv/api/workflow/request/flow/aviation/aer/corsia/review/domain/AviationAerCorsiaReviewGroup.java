package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain;

import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;

import java.util.HashSet;
import java.util.Set;

public enum AviationAerCorsiaReviewGroup {

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
    CONFIDENTIALITY,

    //verification report related
    VERIFIER_DETAILS,
    TIME_ALLOCATION_AND_SCOPE,
    VERIFICATION_CRITERIA,
    PROCESS_AND_ANALYSIS_DETAILS,
    MONITORING_APPROACH_EMISSIONS,
    ELIGIBLE_FUELS_REDUCTION_CLAIM,
    UNCORRECTED_MISSTATEMENTS,
    UNCORRECTED_NON_CONFORMITIES,
    UNCORRECTED_NON_COMPLIANCES,
    RECOMMENDED_IMPROVEMENTS,
    CONCLUSIONS_DATA_QUALITY,
    VERIFICATION_STATEMENT_CONCLUSIONS,
    INDEPENDENT_REVIEW;

    public static Set<AviationAerCorsiaReviewGroup> getVerificationReportDataReviewGroups(AviationAerCorsia aer) {
        Set<AviationAerCorsiaReviewGroup> reviewGroups = new HashSet<>(
            Set.of(
                VERIFIER_DETAILS,
                TIME_ALLOCATION_AND_SCOPE,
                VERIFICATION_CRITERIA,
                PROCESS_AND_ANALYSIS_DETAILS,
                MONITORING_APPROACH_EMISSIONS,
                UNCORRECTED_MISSTATEMENTS,
                UNCORRECTED_NON_CONFORMITIES,
                UNCORRECTED_NON_COMPLIANCES,
                RECOMMENDED_IMPROVEMENTS,
                CONCLUSIONS_DATA_QUALITY,
                VERIFICATION_STATEMENT_CONCLUSIONS,
                INDEPENDENT_REVIEW
            )
        );
        if (aer != null && 
        		aer.getEmissionsReductionClaim() != null &&
                Boolean.TRUE.equals(aer.getEmissionsReductionClaim().getExist())) {
            reviewGroups.add(ELIGIBLE_FUELS_REDUCTION_CLAIM);
        }
        return reviewGroups;
    }

    public static Set<AviationAerCorsiaReviewGroup> getAerDataReviewGroups(boolean isReportingRequired) {
        Set<AviationAerCorsiaReviewGroup> aerReviewGroups = new HashSet<>();

        if (isReportingRequired) {
            aerReviewGroups.add(SERVICE_CONTACT_DETAILS);
            aerReviewGroups.add(OPERATOR_DETAILS);
            aerReviewGroups.add(MONITORING_PLAN_CHANGES);
            aerReviewGroups.add(MONITORING_APPROACH);
            aerReviewGroups.add(AGGREGATED_EMISSIONS_DATA);
            aerReviewGroups.add(AIRCRAFT_DATA);
            aerReviewGroups.add(EMISSIONS_REDUCTION_CLAIM);
            aerReviewGroups.add(TOTAL_EMISSIONS);
            aerReviewGroups.add(ADDITIONAL_DOCUMENTS);
            aerReviewGroups.add(DATA_GAPS);
            aerReviewGroups.add(CONFIDENTIALITY);
        } else {
            aerReviewGroups.add(REPORTING_OBLIGATION_DETAILS);
        }

        return aerReviewGroups;
    }
}
