package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InstallationDetailsType {
    INSTALLATION_EMITTER("Installation emitter"),
    INSTALLATION_DETAILS("Installation details");

    private final String description;
}
