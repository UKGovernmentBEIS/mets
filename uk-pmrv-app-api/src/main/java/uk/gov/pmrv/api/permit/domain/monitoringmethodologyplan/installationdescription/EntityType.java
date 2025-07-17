package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EntityType {
    ETS_INSTALLATION("Installation covered by ETS"),
    NON_ETS_INSTALLATION("Installation outside ETS"),
    NITRIC_ACID_PRODUCTION("Installation producing Nitric Acid"),
    HEAT_DISTRIBUTION_NETWORK("Heat distribution network");

    private final String description;

    public static EntityType getByValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}
