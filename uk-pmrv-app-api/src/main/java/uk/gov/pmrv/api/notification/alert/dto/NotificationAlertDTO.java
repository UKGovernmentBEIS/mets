package uk.gov.pmrv.api.notification.alert.dto;

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
@EqualsAndHashCode
public class NotificationAlertDTO {

	@NotBlank
	private String subject;

	@NotBlank
	private String body;
}
