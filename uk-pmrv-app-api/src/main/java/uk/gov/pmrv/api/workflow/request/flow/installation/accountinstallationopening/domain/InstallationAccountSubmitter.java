package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallationAccountSubmitter {

    @NotBlank(message = "{submitter.name.typeMismatch}")
    @Size(max = 255, message = "{submitter.name.typeMismatch}")
    private String name;

    @Email(message = "{submitter.email.typeMismatch}")
    @Size(max = 255, message = "{submitter.email.typeMismatch}")
    private String email;
}
