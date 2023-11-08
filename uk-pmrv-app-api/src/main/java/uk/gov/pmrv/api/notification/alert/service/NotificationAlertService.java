package uk.gov.pmrv.api.notification.alert.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.notification.alert.dto.NotificationAlertDTO;
import uk.gov.pmrv.api.notification.alert.mapper.NotificationAlertMapper;
import uk.gov.pmrv.api.notification.alert.repository.NotificationAlertRepository;

@Service
@RequiredArgsConstructor
public class NotificationAlertService {

	private final NotificationAlertRepository notificationAlertRepository;
	private final NotificationAlertMapper mapper;

    public List<NotificationAlertDTO> getNotificationAlerts() {
    	LocalDateTime currentDate = LocalDateTime.now();
        return mapper.toNotificationAlertDTO(
        		notificationAlertRepository.findAllByOrderByActiveFromAsc().stream()
        		.filter(alert -> currentDate.isAfter(alert.getActiveFrom()) && currentDate.isBefore(alert.getActiveUntil()))
        		.toList());	
    }
}
