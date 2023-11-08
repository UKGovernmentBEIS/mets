package uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InherentCO2Direction {
    EXPORTED_TO_ETS_INSTALLATION("Exported to ETS installation"),
    EXPORTED_TO_NON_ETS_CONSUMER("Exported to non ETS consumer"),
    RECEIVED_FROM_ANOTHER_INSTALLATION("Received from another installation");

    private final String description;
}
