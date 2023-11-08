package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AviationAerCorsiaFuelDensityType {

    ACTUAL_DENSITY("Actual density"),
    ACTUAL_STANDARD_DENSITY("Actual and standard density"),
    STANDARD_DENSITY("Standard density"),
    NOT_APPLICABLE("Not applicable - we only use the block-off/block-on method")
    ;

    private final String description;
}
