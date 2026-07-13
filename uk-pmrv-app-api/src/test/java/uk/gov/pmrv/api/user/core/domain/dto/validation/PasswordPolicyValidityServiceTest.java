package uk.gov.pmrv.api.user.core.domain.dto.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationErrorDTO;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationRequestDTO;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationResponseDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.PasswordPolicyErrorCodeEnum;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationError;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationResponse;
import uk.gov.pmrv.api.user.core.service.auth.KeycloakValidatePasswordCustomClient;
import uk.gov.pmrv.api.user.core.transform.PasswordValidityMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordPolicyValidityServiceTest {

    @InjectMocks
    private PasswordPolicyValidityService service;

    @Mock
    private KeycloakValidatePasswordCustomClient keycloakCustomClient;

    @Mock
    private PasswordValidityMapper passwordValidityMapper;

    @Test
    void validate_valid_password() {
        String password = "P@ssword123!";
        PasswordValidationRequestDTO requestDTO = PasswordValidationRequestDTO.builder().password(password).build();

        KeycloakPasswordValidationResponse keycloakResponse = KeycloakPasswordValidationResponse.builder()
                .valid(true)
                .errors(List.of())
                .build();

        PasswordValidationResponseDTO expectedResponse = PasswordValidationResponseDTO.builder()
                .valid(true)
                .errors(List.of())
                .build();

        when(keycloakCustomClient.validatePassword(password)).thenReturn(keycloakResponse);
        when(passwordValidityMapper.toPasswordValidationResponseDTO(keycloakResponse)).thenReturn(expectedResponse);

        PasswordValidationResponseDTO result = service.validate(requestDTO);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();

        verify(keycloakCustomClient).validatePassword(password);
        verify(passwordValidityMapper).toPasswordValidationResponseDTO(keycloakResponse);
        verifyNoMoreInteractions(keycloakCustomClient, passwordValidityMapper);
    }

    @Test
    void validate_invalid_password() {
        String password = "short";
        PasswordValidationRequestDTO requestDTO = PasswordValidationRequestDTO.builder().password(password).build();

        KeycloakPasswordValidationResponse keycloakResponse = KeycloakPasswordValidationResponse.builder()
                .valid(false)
                .errors(List.of(
                        KeycloakPasswordValidationError.builder()
                                .code(PasswordPolicyErrorCodeEnum.INVALID_MIN_LENGTH)
                                .message("Password is too short")
                                .build()
                ))
                .build();

        PasswordValidationResponseDTO expectedResponse = PasswordValidationResponseDTO.builder()
                .valid(false)
                .errors(List.of(
                        PasswordValidationErrorDTO.builder()
                                .code(PasswordPolicyErrorCodeEnum.INVALID_MIN_LENGTH)
                                .message("Password is too short")
                                .build()
                ))
                .build();

        when(keycloakCustomClient.validatePassword(password)).thenReturn(keycloakResponse);
        when(passwordValidityMapper.toPasswordValidationResponseDTO(keycloakResponse)).thenReturn(expectedResponse);

        PasswordValidationResponseDTO result = service.validate(requestDTO);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().get(0).getCode()).isEqualTo(PasswordPolicyErrorCodeEnum.INVALID_MIN_LENGTH);

        verify(keycloakCustomClient).validatePassword(password);
        verify(passwordValidityMapper).toPasswordValidationResponseDTO(keycloakResponse);
        verifyNoMoreInteractions(keycloakCustomClient, passwordValidityMapper);
    }
}
