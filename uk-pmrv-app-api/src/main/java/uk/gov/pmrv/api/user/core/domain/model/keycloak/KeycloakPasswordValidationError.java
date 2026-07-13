package uk.gov.pmrv.api.user.core.domain.model.keycloak;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.user.core.domain.enumeration.PasswordPolicyErrorCodeEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakPasswordValidationError {

    private PasswordPolicyErrorCodeEnum code;

    private String message;
}
