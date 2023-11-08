package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferCO2Direction {
    EXPORTED_TO_LONG_TERM_FACILITY("Exported to long term facility"),
    EXPORTED_FOR_PRECIPITATED_CALCIUM("Exported for precipitated calcium"),
    RECEIVED_FROM_ANOTHER_INSTALLATION("Received from another installation");

    private String description;
}
