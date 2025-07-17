package uk.gov.pmrv.api.web.orchestrator.uiconfiguration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.configuration.domain.ui.UIConfigurationPropertiesDTO;
import uk.gov.netz.api.configuration.service.ui.UIConfigurationPropertiesResolver;
import uk.gov.netz.api.alert.dto.NotificationAlertDTO;
import uk.gov.netz.api.alert.service.NotificationAlertService;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UIPropertiesResolverTest {

	@InjectMocks
    private UIPropertiesResolver cut;

    @Mock
    private NotificationAlertService notificationAlertService;
    
    @Mock
    private UIConfigurationPropertiesResolver uiConfigurationPropertiesResolver;
    
    @Test
    void resolve() {
		UIConfigurationPropertiesDTO uiConfigurationPropertiesDTO = UIConfigurationPropertiesDTO.builder()
				.features(new HashMap<String, Boolean>() {
					{
						put("ui.features.key1", Boolean.TRUE);
						put("ui.features.key2", Boolean.FALSE);
					}
				})
				.analytics(new HashMap<String, String>() {
					{
						put("ui.analytics.key1", "analytics1");
						put("ui.analytics.key2", "analytics2");
					}
				})
				.keycloakServerUrl("keycloakServerUrl")
				.build();
		
		List<NotificationAlertDTO> notificationAlerts = List.of(
				NotificationAlertDTO.builder().subject("sub1").body("body1").build(),
				NotificationAlertDTO.builder().subject("sub2").body("body2").build()
				);
		
		when(uiConfigurationPropertiesResolver.resolve()).thenReturn(uiConfigurationPropertiesDTO);
		when(notificationAlertService.getNotificationAlerts()).thenReturn(notificationAlerts);
		
		UIPropertiesDTO result = cut.resolve();
		
		assertThat(result).isEqualTo(UIPropertiesDTO.builder()
				.features(uiConfigurationPropertiesDTO.getFeatures())
				.analytics(uiConfigurationPropertiesDTO.getAnalytics())
				.keycloakServerUrl(uiConfigurationPropertiesDTO.getKeycloakServerUrl())
				.notificationAlerts(notificationAlerts)
				.build());
		
		verify(uiConfigurationPropertiesResolver, times(1)).resolve();
		verify(notificationAlertService, times(1)).getNotificationAlerts();
    }
}
