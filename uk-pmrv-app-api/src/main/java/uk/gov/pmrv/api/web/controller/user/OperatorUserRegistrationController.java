package uk.gov.pmrv.api.web.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.user.core.domain.dto.EmailDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAcceptInvitationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserActivationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserRegistrationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserTokenVerificationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.ENABLE_OPERATOR_USER_FROM_INVITATION_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REGISTER_OPERATOR_USER_FROM_INVITATION_WOUT_CREDENTIALS_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.USERS_INVITATION_TOKEN_VERIFICATION_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.USERS_TOKEN_VERIFICATION_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.VALIDATION_ERROR_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/operator-users/registration")
@Tag(name = "Operator users registration")
@SecurityRequirements
@RequiredArgsConstructor
public class OperatorUserRegistrationController {

    private final OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    private final OperatorUserRegistrationService operatorUserRegistrationService;
    private final OperatorUserActivationService operatorUserActivationService;
    private final OperatorUserAcceptInvitationService operatorUserAcceptInvitationService;

    /**
     * Sends a verification email to the provided email.
     *
     * @param emailDTO {@link EmailDTO}
     */
    @PostMapping(path = "/verification-email")
    @Operation(summary = "Sends a verification email")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = VALIDATION_ERROR_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> sendVerificationEmail(
            @RequestBody @Valid @Parameter(description = "The user email", required = true) EmailDTO emailDTO) {
        operatorUserRegistrationService.sendVerificationEmail(emailDTO.getEmail());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Verifies the JWT token.
     *
     * @param tokenDTO {@link TokenDTO}
     * @return {@link EmailDTO}
     */
    @PostMapping(path = "/token-verification")
    @Operation(summary = "Verifies the JWT token provided in the email")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmailDTO.class))})
    @ApiResponse(responseCode = "400", description = USERS_TOKEN_VERIFICATION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<EmailDTO> verifyUserRegistrationToken
            (@RequestBody @Valid @Parameter(description = "The verification token", required = true) TokenDTO tokenDTO) {
        String email = operatorUserTokenVerificationService.verifyRegistrationToken(tokenDTO.getToken());
        return new ResponseEntity<>(new EmailDTO(email), HttpStatus.OK);
    }

    /**
     * Registers an operator user.
     * @param userRegistrationDTO a userRegistrationDTO.
     * @return {@link OperatorUserDTO}
     */
    @PostMapping(path = "/register")
    @Operation(summary = "Register a new operator user")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = VALIDATION_ERROR_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<OperatorUserDTO> registerUser(@RequestBody @Valid @Parameter(description = "The userRegistrationDTO", required = true)
                                                                OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO) {
        return new ResponseEntity<>(operatorUserRegistrationService.registerUser(userRegistrationDTO), HttpStatus.OK);
    }

    /**
     * Registers a new operator user from invitation token.
     *
     * @param operatorUserRegistrationWithCredentialsDTO {@link OperatorUserRegistrationWithCredentialsDTO}.
     */
    @PutMapping(path = "/register-from-invitation")
    @Operation(summary = "Registers a new operator user from invitation token")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = USERS_INVITATION_TOKEN_VERIFICATION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<OperatorUserDTO> registerNewUserFromInvitationWithCredentials(
            @RequestBody @Valid @Parameter(description = "The operator user", required = true)
                    OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO) {
        return new ResponseEntity<>(operatorUserActivationService.activateAndEnableOperatorInvitedUser(
                operatorUserRegistrationWithCredentialsDTO), HttpStatus.OK);
    }

    @PutMapping(path = "/register-from-invitation-no-credentials")
    @Operation(summary = "Registers a new operator user from invitation token without credentials")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = REGISTER_OPERATOR_USER_FROM_INVITATION_WOUT_CREDENTIALS_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<OperatorUserDTO> registerNewUserFromInvitation(
            @RequestBody @Valid @Parameter(description = "The operator user", required = true)
                    OperatorUserRegistrationDTO operatorUserRegistrationDTO) {
        return new ResponseEntity<>(operatorUserActivationService
                .activateOperatorInvitedUser(operatorUserRegistrationDTO), HttpStatus.OK);
    }

    @PutMapping(path = "/enable-from-invitation")
    @Operation(summary = "Enables a new operator user from invitation")
    @ApiResponse(responseCode = "204", description = NO_CONTENT, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = ENABLE_OPERATOR_USER_FROM_INVITATION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> enableOperatorInvitedUser(
            @RequestBody @Valid @Parameter(description = "The operator user credentials", required = true)
                    InvitedUserEnableDTO invitedUserEnableDTO) {
        operatorUserActivationService.enableOperatorInvitedUser(invitedUserEnableDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "/accept-invitation")
    @Operation(summary = "Accept invitation for operator user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorInvitedUserInfoDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_OPERATOR_INVITATION_TOKEN_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<OperatorInvitedUserInfoDTO> acceptOperatorInvitation(
            @RequestBody @Valid @Parameter(description = "The invitation token", required = true) TokenDTO invitationTokenDTO) {
        OperatorInvitedUserInfoDTO operatorInvitedUserInfo =
                operatorUserAcceptInvitationService.acceptInvitation(invitationTokenDTO.getToken());
        return new ResponseEntity<>(operatorInvitedUserInfo, HttpStatus.OK);
    }
}
