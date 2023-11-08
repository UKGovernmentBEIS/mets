package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferN2ODirection {
    EXPORTED_TO_LONG_TERM_FACILITY("Exported to long term facility"),
    RECEIVED_FROM_ANOTHER_INSTALLATION("Received from another installation");

    private final String description;
}
