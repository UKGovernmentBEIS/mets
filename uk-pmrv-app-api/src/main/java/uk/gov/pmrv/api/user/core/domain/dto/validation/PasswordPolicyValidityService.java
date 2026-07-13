package uk.gov.pmrv.api.user.core.domain.dto.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationRequestDTO;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationResponseDTO;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationResponse;
import uk.gov.pmrv.api.user.core.service.auth.KeycloakValidatePasswordCustomClient;
import uk.gov.pmrv.api.user.core.transform.PasswordValidityMapper;

@Service
@RequiredArgsConstructor
public class PasswordPolicyValidityService {

    private final KeycloakValidatePasswordCustomClient keycloakCustomClient;
    private final PasswordValidityMapper passwordValidityMapper;

    public PasswordValidationResponseDTO validate(PasswordValidationRequestDTO requestDTO) {
        KeycloakPasswordValidationResponse keycloakResponse = keycloakCustomClient.validatePassword(requestDTO.getPassword());

        return passwordValidityMapper.toPasswordValidationResponseDTO(keycloakResponse);
    }
}
