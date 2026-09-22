package uk.gov.pmrv.api.integration.registry.notification.installation.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class ReturnOfAllowancesInstallationNotificationRegistryEvent extends InstallationNotificationRegistryEvent{

    private LocalDate returnOfAllowancesDate;

}
