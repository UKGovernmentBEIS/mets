package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum NotFollowingHierarchicalOrderReason {
    OTHER_DATA_SOURCES_LEAD_TO_LOWER_UNCERTAINTY(
            "Uncertainty assessment"),
    USE_OF_BETTER_DATA_SOURCES_TECHNICALLY_INFEASIBLE(
            "Technically infeasible"),
    USE_OF_BETTER_DATA_SOURCES_UNREASONABLE_COSTS(
            "Unreasonable costs");

    private final String description;

    public static NotFollowingHierarchicalOrderReason getByValue(String value) {
        return Arrays.stream(values())
                .filter(reason -> reason.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}