package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AviationAerCorsiaMonitoringApproachType {

    CERT_MONITORING("CERT only"),
    FUEL_USE_MONITORING("Fuel use monitoring only"),
    CERT_AND_FUEL_USE_MONITORING("CERT and fuel use monitoring");

    private final String description;
}
