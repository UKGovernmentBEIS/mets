package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum InstallationConnectionType {
    MEASURABLE_HEAT("Measurable heat"),
    WASTE_GAS("Waste gas"),
    TRANSFERRED_CO2_FOR_USE_IN_YOUR_INSTALLATION_CCU("transferred CO2"),
    TRANSFERRED_CO2_FOR_STORAGE("Transferred CO2 for geological storage (CCS)"),
    INTERMEDIATE_PRODUCTS_COVERED_BY_PRODUCT_BENCHMARKS("Intermediate products");

    private final String description;

    public static InstallationConnectionType getByValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
