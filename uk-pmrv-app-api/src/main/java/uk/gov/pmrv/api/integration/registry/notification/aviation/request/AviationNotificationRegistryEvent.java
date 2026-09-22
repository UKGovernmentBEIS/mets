package uk.gov.pmrv.api.integration.registry.notification.aviation.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.integration.registry.notification.common.NotificationRegistryEvent;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AviationNotificationRegistryEvent extends NotificationRegistryEvent {
}
