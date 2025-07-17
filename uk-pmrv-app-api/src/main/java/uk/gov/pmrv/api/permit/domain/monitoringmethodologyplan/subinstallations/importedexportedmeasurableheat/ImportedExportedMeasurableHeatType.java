package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ImportedExportedMeasurableHeatType {
    MEASURABLE_HEAT_IMPORTED("Measurable heat imported"),
    MEASURABLE_HEAT_FROM_PULP("Measurable heat from pulp"),
    MEASURABLE_HEAT_FROM_NITRIC_ACID("Measurable heat from nitric acid"),
    MEASURABLE_HEAT_EXPORTED("Measurable heat exported"),
    NO_MEASURABLE_HEAT("No, we do not have any measurable heat imported to or exported from this sub-installation");

    private final String description;

    public static ImportedExportedMeasurableHeatType getByValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
