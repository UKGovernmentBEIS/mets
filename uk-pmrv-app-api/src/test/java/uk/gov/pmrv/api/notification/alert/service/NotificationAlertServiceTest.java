package uk.gov.pmrv.api.notification.alert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.notification.alert.domain.NotificationAlert;
import uk.gov.pmrv.api.notification.alert.dto.NotificationAlertDTO;
import uk.gov.pmrv.api.notification.alert.mapper.NotificationAlertMapper;
import uk.gov.pmrv.api.notification.alert.repository.NotificationAlertRepository;

@ExtendWith(MockitoExtension.class)
class NotificationAlertServiceTest {

	@InjectMocks
    private NotificationAlertService service;

    @Mock
    private NotificationAlertRepository repository;
    
    @Mock
    private NotificationAlertMapper mapper;

    @Test
    void getNotificationAlerts() {
    	NotificationAlert alert1 = new NotificationAlert();
    	alert1.setId(1L);
        alert1.setBody("body1");
        alert1.setSubject("subject1");
        alert1.setActiveFrom(LocalDateTime.now().minusDays(2));
        alert1.setActiveUntil(LocalDateTime.now().plusDays(2));
        
        NotificationAlert alert2 = new NotificationAlert();
        alert2.setId(2L);
        alert2.setBody("body2");
        alert2.setSubject("subject2");
        alert2.setActiveFrom(LocalDateTime.now().plusDays(2));
        alert2.setActiveUntil(LocalDateTime.now().plusDays(3));
        
        when(repository.findAllByOrderByActiveFromAsc()).thenReturn(List.of(alert1, alert2));
        when(mapper.toNotificationAlertDTO(List.of(alert1))).thenReturn(List.of(
        		NotificationAlertDTO.builder().subject("subject1").body("body1").build()));

        List<NotificationAlertDTO> alertDtos = service.getNotificationAlerts();

        assertThat(alertDtos).hasSize(1);
        assertThat(alertDtos.get(0).getSubject()).isEqualTo("subject1");
        assertThat(alertDtos.get(0).getBody()).isEqualTo("body1");
    }
}
