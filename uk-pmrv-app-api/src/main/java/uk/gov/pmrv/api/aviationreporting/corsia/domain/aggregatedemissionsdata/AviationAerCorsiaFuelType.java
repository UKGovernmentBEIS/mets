package uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum AviationAerCorsiaFuelType {

    JET_KEROSENE("Jet kerosene (JET A1 or JET A)", new BigDecimal("3.16")),
    JET_GASOLINE("Jet gasoline (JET B)", new BigDecimal("3.10")),
    AVIATION_GASOLINE("Aviation gasoline (AV Gas)", new BigDecimal("3.10")),
    TS_1("TS-1", new BigDecimal("3.16")),
    NO_3_JET_FUEL("No.3 Jet Fuel", new BigDecimal("3.16"))
    ;

    private final String description;
    private final BigDecimal emissionFactor;
}
