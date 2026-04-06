package uk.gov.pmrv.api.integration.registry.notification.installation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRegistryEvent {

    private Long accountId;
    private String requestId;
    private FileInfoDTO fileInfoDTO;
    private RegistryNotificationType registryNotificationType;

}
