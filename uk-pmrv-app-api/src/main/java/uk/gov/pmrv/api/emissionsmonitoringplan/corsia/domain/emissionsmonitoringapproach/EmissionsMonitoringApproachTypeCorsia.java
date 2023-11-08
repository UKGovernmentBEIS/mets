package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmissionsMonitoringApproachTypeCorsia {

    CERT_MONITORING("Use CORSIA CO2 estimation and reporting tool (CERT)"),
    FUEL_USE_MONITORING("Use fuel use monitoring")
    ;

    private String description;
}
