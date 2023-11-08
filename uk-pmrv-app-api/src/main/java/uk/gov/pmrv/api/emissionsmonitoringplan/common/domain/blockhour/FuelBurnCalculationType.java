package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FuelBurnCalculationType {

    CLEAR_DISTINGUISHION("You can clearly distinguish between fuel uplifts for international and domestic flights on a flight by flight basis"),
    NOT_CLEAR_DISTINGUISHION("You can clearly distinguish between fuel uplifts for international and domestic flights on a flight by flight basis")
    ;

    private String description;
}
