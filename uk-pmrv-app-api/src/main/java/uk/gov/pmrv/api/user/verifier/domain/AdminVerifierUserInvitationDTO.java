package uk.gov.pmrv.api.user.verifier.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class AdminVerifierUserInvitationDTO extends UserDTO {

    @NotBlank(message = "{phoneNumber.number.notEmpty}")
    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String phoneNumber;

    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String mobileNumber;

}
