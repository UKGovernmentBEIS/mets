package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FlowDirection {
    IMPORT("Import"),
    EXPORT("Export");

    private final String description;

    public static FlowDirection getByValue(String value) {
        return Arrays.stream(values())
                .filter(direction -> direction.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}

