package uk.gov.pmrv.api.web.orchestrator.uiconfiguration;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.configuration.service.ui.UIConfigurationPropertiesResolver;
import uk.gov.netz.api.alert.service.NotificationAlertService;

@Service
@RequiredArgsConstructor
public class UIPropertiesResolver {
	
	private final NotificationAlertService notificationAlertService;
	private final UIConfigurationPropertiesResolver uiConfigurationPropertiesResolver;
	private static final UIPropertiesMapper uiPropertiesMapper = Mappers.getMapper(UIPropertiesMapper.class);
	
	public UIPropertiesDTO resolve(){
		return uiPropertiesMapper.toUIPropertiesDTO(uiConfigurationPropertiesResolver.resolve())
				.withNotificationAlerts(notificationAlertService.getNotificationAlerts());
	}
	
}
