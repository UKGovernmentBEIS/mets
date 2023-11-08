package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmissionsMonitoringApproachType {

    EUROCONTROL_SUPPORT_FACILITY("Use unmodified Eurocontrol Support Facility data"),
    EUROCONTROL_SMALL_EMITTERS("Use your own flight data with the Eurocontrol Small Emitters Tool"),
    FUEL_USE_MONITORING("Use fuel use monitoring")
    ;

    private String description;
}
