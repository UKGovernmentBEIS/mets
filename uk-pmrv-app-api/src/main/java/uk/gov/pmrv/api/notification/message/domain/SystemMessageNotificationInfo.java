package uk.gov.pmrv.api.notification.message.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemMessageNotificationInfo {

    private SystemMessageNotificationType messageType;
    private Map<String, Object> messageParameters;
    private Long accountId;
    private String receiver;
}
