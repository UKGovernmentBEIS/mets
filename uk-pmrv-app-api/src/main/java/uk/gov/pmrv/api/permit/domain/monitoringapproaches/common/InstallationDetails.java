package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InstallationDetails {

    @NotBlank(message = "{account.name.notEmpty}")
    @Size(max = 255, message = "{account.name.typeMismatch}")
    private String installationName;

    @NotBlank(message = "{address.line1.notEmpty}")
    @Size(max = 255, message = "{address.line1.typeMismatch}")
    private String line1;

    @Size(max = 255, message = "{address.line2.typeMismatch}")
    private String line2;

    @NotBlank(message = "{address.city.notEmpty}")
    @Size(max = 255, message = "{address.city.typeMismatch}")
    private String city;

    @NotBlank(message = "{address.postcode.notEmpty}")
    @Size(max = 64, message = "{address.postcode.typeMismatch}")
    private String postcode;

    @Email(message = "{permit.monitoringapproach.common.email.typeMismatch}")
    @NotBlank(message = "{permit.monitoringapproach.common.email.notEmpty}")
    private String email;
}
