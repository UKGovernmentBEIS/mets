package uk.gov.pmrv.api.user.core.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationErrorDTO;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationResponseDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.PasswordPolicyErrorCodeEnum;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationError;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordValidityMapperTest {

    private final PasswordValidityMapper mapper = Mappers.getMapper(PasswordValidityMapper.class);

    @Test
    void toPasswordValidationResponseDTO_valid() {
        KeycloakPasswordValidationResponse response = KeycloakPasswordValidationResponse.builder()
                .valid(true)
                .errors(List.of())
                .build();

        PasswordValidationResponseDTO result = mapper.toPasswordValidationResponseDTO(response);

        assertThat(result).isEqualTo(PasswordValidationResponseDTO.builder()
                .valid(true)
                .errors(List.of())
                .build());
    }

    @Test
    void toPasswordValidationResponseDTO_with_errors() {
        KeycloakPasswordValidationResponse response = KeycloakPasswordValidationResponse.builder()
                .valid(false)
                .errors(List.of(
                        KeycloakPasswordValidationError.builder()
                                .code(PasswordPolicyErrorCodeEnum.INVALID_MIN_LENGTH)
                                .message("Password is too short")
                                .build(),
                        KeycloakPasswordValidationError.builder()
                                .code(PasswordPolicyErrorCodeEnum.BLACKLISTED_PATTERN)
                                .message("Password contains a blacklisted pattern")
                                .build()
                ))
                .build();

        PasswordValidationResponseDTO result = mapper.toPasswordValidationResponseDTO(response);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).containsExactly(
                PasswordValidationErrorDTO.builder()
                        .code(PasswordPolicyErrorCodeEnum.INVALID_MIN_LENGTH)
                        .message("Password is too short")
                        .build(),
                PasswordValidationErrorDTO.builder()
                        .code(PasswordPolicyErrorCodeEnum.BLACKLISTED_PATTERN)
                        .message("Password contains a blacklisted pattern")
                        .build()
        );
    }
}
