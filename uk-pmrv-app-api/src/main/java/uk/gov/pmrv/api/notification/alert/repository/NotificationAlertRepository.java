package uk.gov.pmrv.api.notification.alert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.pmrv.api.notification.alert.domain.NotificationAlert;

/**
 * Data repository for the notification alerts.
 */
public interface NotificationAlertRepository extends JpaRepository<NotificationAlert, Long> {

	List<NotificationAlert> findAllByOrderByActiveFromAsc();
}
