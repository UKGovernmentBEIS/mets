package uk.gov.pmrv.api.user.operator.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.user.core.domain.dto.validation.Password;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class OperatorUserRegistrationWithCredentialsDTO extends OperatorUserRegistrationDTO {

    @NotBlank(message = "{userAccount.password.notEmpty}")
    @Password(message = "{userAccount.password.typeMismatch}")
    private String password;
}
