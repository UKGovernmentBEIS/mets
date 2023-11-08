package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class EmissionsMonitoringPlanViolation {

    private String sectionName;
    private String message;
    private Object[] data;

    public EmissionsMonitoringPlanViolation(String sectionName, EmissionsMonitoringPlanViolation.ViolationMessage violationMessage) {
        this(sectionName, violationMessage, List.of());
    }

    public EmissionsMonitoringPlanViolation(String sectionName, EmissionsMonitoringPlanViolation.ViolationMessage violationMessage,
                                            Object... data) {
        this.sectionName = sectionName;
        this.message = violationMessage.getMessage();
        this.data = data;
    }

    @Getter
    public enum ViolationMessage {
        INVALID_MANAGEMENT_PROCEDURES("Management procedures and monitoring approach are not mutually valid"),
        INVALID_ISSUING_AUTHORITY("Invalid issuing authority"),
        INVALID_FUEL_CONSUMPTION_MEASURING_METHOD("Fuel consumption measuring method and monitoring approach are not mutually valid"),
        INVALID_ADDITIONAL_AIRCRAFT_MONITORING_APPROACH("Additional aircraft monitoring approach and monitoring approach are not mutually valid"),
        INVALID_MULTIPLE_FUEL_CONSUMPTION_MEASURING_METHODS_EXPLANATION("Explanation should exist when using multiple fuel consumption measuring methods"),
        INVALID_METHOD_A_PROCEDURES("Method A procedures and fuel consumption measuring method are not mutually valid"),
        INVALID_METHOD_B_PROCEDURES("Method B procedures and fuel consumption measuring method are not mutually valid"),
        INVALID_BLOCK_ON_BLOCK_OFF_METHOD_PROCEDURES("Block on block off method procedures and fuel consumption measuring method are not mutually valid"),
        INVALID_FUEL_UPLIFT_METHOD_PROCEDURES("Fuel uplift method procedures and fuel consumption measuring method are not mutually valid"),
        INVALID_DATA_GAPS("Emissions monitoring approach type and data gaps are not mutually valid"),
        INVALID_BLOCK_HOUR_METHOD_PROCEDURES("Block hour method procedures and fuel consumption measuring method are not mutually valid")

        ;

        private final String message;

        ViolationMessage(String message) {
            this.message = message;
        }
    }
}
