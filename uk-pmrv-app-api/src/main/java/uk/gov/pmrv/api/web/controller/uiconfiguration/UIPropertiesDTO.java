package uk.gov.pmrv.api.web.controller.uiconfiguration;

import lombok.Builder;
import lombok.Data;
import uk.gov.pmrv.api.notification.alert.dto.NotificationAlertDTO;

import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Validated
@Data
@Builder
public class UIPropertiesDTO {
	private Map<String, Boolean> features;
	private Map<String, String> analytics;
	private List<NotificationAlertDTO> notificationAlerts;
}
