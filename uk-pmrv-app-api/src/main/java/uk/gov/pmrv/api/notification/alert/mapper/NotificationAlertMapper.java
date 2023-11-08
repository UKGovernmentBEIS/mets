package uk.gov.pmrv.api.notification.alert.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.notification.alert.domain.NotificationAlert;
import uk.gov.pmrv.api.notification.alert.dto.NotificationAlertDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface NotificationAlertMapper {

	List<NotificationAlertDTO> toNotificationAlertDTO(List<NotificationAlert> notificationAlerts);
}
