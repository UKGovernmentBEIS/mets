package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain;

import java.util.Set;

public enum EmpCorsiaReviewGroup {

    SERVICE_CONTACT_DETAILS,
    OPERATOR_DETAILS,
    FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES,
    MONITORING_APPROACH,
    EMISSION_SOURCES,
    MANAGEMENT_PROCEDURES,
    ABBREVIATIONS_AND_DEFINITIONS,
    ADDITIONAL_DOCUMENTS,
    DATA_GAPS,

    METHOD_A_PROCEDURES,
    METHOD_B_PROCEDURES,
    BLOCK_ON_OFF_PROCEDURES,
    FUEL_UPLIFT_PROCEDURES,
    BLOCK_HOUR_PROCEDURES,
    ;

    public static Set<EmpCorsiaReviewGroup> getStandardReviewGroups(){
        return Set.of(
            EmpCorsiaReviewGroup.SERVICE_CONTACT_DETAILS,
            EmpCorsiaReviewGroup.OPERATOR_DETAILS,
            EmpCorsiaReviewGroup.FLIGHT_AND_AIRCRAFT_MONITORING_PROCEDURES,
            EmpCorsiaReviewGroup.MONITORING_APPROACH,
            EmpCorsiaReviewGroup.EMISSION_SOURCES,
            EmpCorsiaReviewGroup.MANAGEMENT_PROCEDURES,
            EmpCorsiaReviewGroup.ABBREVIATIONS_AND_DEFINITIONS,
            EmpCorsiaReviewGroup.ADDITIONAL_DOCUMENTS,
            EmpCorsiaReviewGroup.DATA_GAPS
        );
    }
}
