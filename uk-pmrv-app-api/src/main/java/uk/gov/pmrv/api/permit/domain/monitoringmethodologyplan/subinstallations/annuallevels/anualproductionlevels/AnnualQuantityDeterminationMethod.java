package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnnualQuantityDeterminationMethod {
    CONTINUAL_METERING_PROCESS("5. (a) based on continual metering at the process where the material is consumed or produced"),
    AGGREGATION_METERING_QUANTITIES("5. (b) based on aggregation of metering of quantities separately delivered or produced taking into account relevant stock changes");

    private final String description;

    public static AnnualQuantityDeterminationMethod getByValue(String value) {
        return Arrays.stream(values())
                .filter(method -> method.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
