package uk.gov.pmrv.api.web.orchestrator.uiconfiguration;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.alert.dto.NotificationAlertDTO;

import java.util.List;
import java.util.Map;

@Validated
@Data
@Builder
public class UIPropertiesDTO {
	private Map<String, Boolean> features;
	private Map<String, String> analytics;
	private String keycloakServerUrl;
	
	@With
	private List<NotificationAlertDTO> notificationAlerts;
}
