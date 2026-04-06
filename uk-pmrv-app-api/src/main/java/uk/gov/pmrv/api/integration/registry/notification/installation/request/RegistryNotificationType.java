package uk.gov.pmrv.api.integration.registry.notification.installation.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegistryNotificationType {

    SURRENDER_NOTIFICATION("Installation surrender request approval"),
    TRANSFER_NOTIFICATION("Installation transfer"),
    SURRENDER_CESSATION_NOTIFICATION("Installation surrender cessation completed");

    private final String name;

}
