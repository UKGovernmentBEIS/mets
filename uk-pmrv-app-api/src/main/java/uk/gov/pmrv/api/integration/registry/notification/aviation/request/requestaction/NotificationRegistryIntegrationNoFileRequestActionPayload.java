package uk.gov.pmrv.api.integration.registry.notification.aviation.request.requestaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class NotificationRegistryIntegrationNoFileRequestActionPayload extends RequestActionPayload {

    private Integer registryId;
    private String notificationType;

}
