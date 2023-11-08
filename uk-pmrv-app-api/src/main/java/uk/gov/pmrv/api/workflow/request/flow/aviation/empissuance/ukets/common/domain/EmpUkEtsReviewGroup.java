package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain;

import java.util.Set;

public enum EmpUkEtsReviewGroup {

    SERVICE_CONTACT_DETAILS,
    OPERATOR_DETAILS,
    FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES,
    MONITORING_APPROACH,
    EMISSION_SOURCES,
    EMISSIONS_REDUCTION_CLAIM,
    MANAGEMENT_PROCEDURES,
    ABBREVIATIONS_AND_DEFINITIONS,
    ADDITIONAL_DOCUMENTS,
    LATE_SUBMISSION,

    METHOD_A_PROCEDURES,
    METHOD_B_PROCEDURES,
    BLOCK_ON_OFF_PROCEDURES,
    FUEL_UPLIFT_PROCEDURES,
    BLOCK_HOUR_PROCEDURES,
    DATA_GAPS
    ;

    public static Set<EmpUkEtsReviewGroup> getStandardReviewGroups(){
        return Set.of(
            EmpUkEtsReviewGroup.SERVICE_CONTACT_DETAILS,
            EmpUkEtsReviewGroup.OPERATOR_DETAILS,
            EmpUkEtsReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES,
            EmpUkEtsReviewGroup.MONITORING_APPROACH,
            EmpUkEtsReviewGroup.EMISSION_SOURCES,
            EmpUkEtsReviewGroup.EMISSIONS_REDUCTION_CLAIM,
            EmpUkEtsReviewGroup.MANAGEMENT_PROCEDURES,
            EmpUkEtsReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
            EmpUkEtsReviewGroup.ADDITIONAL_DOCUMENTS,
            EmpUkEtsReviewGroup.LATE_SUBMISSION
        );
    }
}
