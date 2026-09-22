package uk.gov.pmrv.api.integration.registry.notification.installation.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.integration.registry.notification.common.NotificationRegistryEvent;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InstallationNotificationRegistryEvent extends NotificationRegistryEvent {
}
