package uk.gov.pmrv.api.integration.registry.notification.installation.request.requestaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class ReturnOfAllowancesNotificationRegistryIntegrationRequestActionPayload extends NotificationRegistryIntegrationRequestActionPayload {

    private LocalDate returnOfAllowancesDate;

}
