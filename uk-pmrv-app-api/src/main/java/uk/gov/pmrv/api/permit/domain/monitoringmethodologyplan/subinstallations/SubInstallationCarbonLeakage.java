package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SubInstallationCarbonLeakage {
    EXPOSED("Exposed"),
    NOT_EXPOSED("Not exposed");

    private final String description;

    public static SubInstallationCarbonLeakage getByValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
