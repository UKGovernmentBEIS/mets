package uk.gov.pmrv.api.user.core.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitedUserCredentialsDTO {

    @NotBlank(message = "{jwt.token.notEmpty}")
    private String invitationToken;

    @NotBlank(message = "{userAccount.password.notEmpty}")
    private String password;
}
