package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallationEmitter {

    @NotBlank
    private String emitterId;

    @Email(message = "{permit.monitoringapproach.common.email.typeMismatch}")
    @NotBlank(message = "{permit.monitoringapproach.common.email.notEmpty}")
    private String email;
}
