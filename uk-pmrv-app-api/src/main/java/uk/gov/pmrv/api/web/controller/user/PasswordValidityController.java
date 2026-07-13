package uk.gov.pmrv.api.web.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationRequestDTO;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationResponseDTO;
import uk.gov.pmrv.api.user.core.domain.dto.validation.PasswordPolicyValidityService;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.VALIDATION_ERROR_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/users/validate-password")
@Tag(name = "Validate Password")
@RequiredArgsConstructor
public class PasswordValidityController {

    private final PasswordPolicyValidityService passwordPolicyValidityService;

    /**
     * Checks whether the given password complies with the configured password policies.
     */
    @PostMapping
    @Operation(summary = "Check if password matches the password policy rules")
    @ApiResponse(responseCode = "200", description = OK,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PasswordValidationResponseDTO.class))})
    @ApiResponse(responseCode = "400", description = VALIDATION_ERROR_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<PasswordValidationResponseDTO> validatePassword(
            @RequestBody @Valid @Parameter(description = "The password validation request", required = true) PasswordValidationRequestDTO passwordValidationRequest) {

        return new ResponseEntity<>(passwordPolicyValidityService.validate(passwordValidationRequest), HttpStatus.OK);
    }

}
