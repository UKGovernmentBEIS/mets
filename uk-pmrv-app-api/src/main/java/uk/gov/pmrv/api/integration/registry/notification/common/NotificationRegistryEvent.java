package uk.gov.pmrv.api.integration.registry.notification.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRegistryEvent {

    private Long accountId;
    private String requestId;
    private FileInfoDTO fileInfoDTO;
    private RegistryNotificationType registryNotificationType;

}
