package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AnnexVIISection72 {
    MEASUREMENTS("7.2. Method 1: Using measurements","7.2.(a)"),
    DOCUMENTATION("7.2. Method 2: Using documentation","7.2.(b)"),
    PROXY_MEASURED_EFFICIENCY("7.2. Method 3: Calculation of a proxy based on measured efficiency","7.2.(c)"),
    PROXY_REFERENCE_EFFICIENCY("7.2. Method 4: Calculating a proxy based on the reference efficiency","7.2.(d)");

    private final String description;
    private final String code;

    public static AnnexVIISection72 getByValue(String value) {
        return Arrays.stream(values())
                .filter(section -> section.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
