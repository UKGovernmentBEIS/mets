package uk.gov.pmrv.api.notification.alert.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode
@Table(name = "notification_alert")
public class NotificationAlert {

	@Id
	@SequenceGenerator(name = "notification_alert_generator", sequenceName = "notification_alert_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_alert_generator")
	private Long id;

	@Column(name = "subject")
	@NotBlank
	private String subject;

	@Column(name = "body")
	@NotBlank
	private String body;

	@Column(name = "active_from")
	@NotBlank
	private LocalDateTime activeFrom;
	
	@Column(name = "active_until")
	@NotBlank
	private LocalDateTime activeUntil;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "creation_date")
	private LocalDateTime creationDate;
}
