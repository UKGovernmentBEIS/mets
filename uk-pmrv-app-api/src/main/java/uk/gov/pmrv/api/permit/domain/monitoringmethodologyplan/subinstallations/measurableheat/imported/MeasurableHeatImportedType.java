package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatType;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MeasurableHeatImportedType {
    MEASURABLE_HEAT_IMPORTED_OTHER_SOURCES("Measurable heat imported from other sources"),
    MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK("Measurable heat imported from product benchmark"),
    MEASURABLE_HEAT_IMPORTED_PULP("Measurable heat imported from pulp"),
    MEASURABLE_HEAT_IMPORTED_FUEL_BENCHMARK("Measurable heat imported from fuel benchmark"),
    MEASURABLE_HEAT_IMPORTED_WASTE_GAS("Measurable heat imported from waste gas"),
    MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED("No, we do not have any measurable heat imported to this sub-installation");

    private final String description;

    public static MeasurableHeatImportedType getByValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
