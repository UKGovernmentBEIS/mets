package uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum AviationAerUkEtsFuelType {

    JET_KEROSENE("Jet kerosene (JET A1 or JET A)", new BigDecimal("3.15"), new BigDecimal("44.10")),
    JET_GASOLINE("Jet gasoline (JET B)", new BigDecimal("3.10"), new BigDecimal("44.30")),
    AVIATION_GASOLINE("Aviation gasoline (AV Gas)", new BigDecimal("3.10"), new BigDecimal("43.30"))
    ;

    private final String description;
    private final BigDecimal emissionFactor;
    private final BigDecimal netCalorificValue;
}
