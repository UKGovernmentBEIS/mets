package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CertEmissionsType {

    GREAT_CIRCLE_DISTANCE("Great Circle Distance"),
    BLOCK_TIME("Block-time")
    ;

    private final String description;
}