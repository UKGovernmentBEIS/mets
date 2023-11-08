package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsiaVerificationTeamLeader {

    @NotBlank
    @Size(max = 500)
    private String name;

    @NotBlank
    @Size(max = 500)
    private String position;

    @NotBlank
    @Size(max = 500)
    private String role;

    @NotBlank
    @Size(max = 256)
    @Email
    private String email;
}
