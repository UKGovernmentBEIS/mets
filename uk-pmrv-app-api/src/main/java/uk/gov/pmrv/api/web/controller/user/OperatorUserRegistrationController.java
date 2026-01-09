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
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.user.core.domain.dto.EmailDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserTokenVerificationResult;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAcceptInvitationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserActivationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserRegistrationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserTokenVerificationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

@RestController
@RequestMapping(path = "/v1.0/operator-users/registration")
@Tag(name = "Operator users registration")
@SecurityRequirements
@RequiredArgsConstructor
@Log4j2
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
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.VALIDATION_ERROR_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> sendVerificationEmail(
            @RequestBody @Valid @Parameter(description = "The user email", required = true) EmailDTO emailDTO) {
        log.debug("Call to sendVerificationEmail: {}", emailDTO);
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
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserTokenVerificationResult.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.USERS_TOKEN_VERIFICATION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<OperatorUserTokenVerificationResult> verifyUserRegistrationToken
            (@RequestBody @Valid @Parameter(description = "The verification token", required = true) TokenDTO tokenDTO) {
        log.debug("Call to verifyUserRegistrationToken: {}", tokenDTO);
		return new ResponseEntity<>(operatorUserTokenVerificationService
				.verifyRegistrationTokenAndResolveAndValidateUserExistence(tokenDTO.getToken()), HttpStatus.OK);
    }

    /**
     * Registers an operator user.
     * @param userRegistrationDTO a userRegistrationDTO.
     * @return {@link OperatorUserDTO}
     */
    @PostMapping(path = "/register")
    @Operation(summary = "Register a new operator user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.VALIDATION_ERROR_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<OperatorUserDTO> registerUser(@RequestBody @Valid @Parameter(description = "The userRegistrationDTO", required = true)
                                                                OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO) {
        log.debug("Call to registerUser: {}", userRegistrationDTO);
        return new ResponseEntity<>(operatorUserRegistrationService.registerUser(userRegistrationDTO), HttpStatus.OK);
    }
    
    
    /**Register from invite endpoints */ 
    
    @PostMapping(path = "/accept-invitation")
    @Operation(summary = "Accept invitation for operator user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorInvitedUserInfoDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_OPERATOR_INVITATION_TOKEN_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<OperatorInvitedUserInfoDTO> acceptOperatorInvitation(
            @RequestBody @Valid @Parameter(description = "The invitation token", required = true) TokenDTO invitationTokenDTO) {
        OperatorInvitedUserInfoDTO operatorInvitedUserInfo =
                operatorUserAcceptInvitationService.acceptInvitation(invitationTokenDTO.getToken());
        log.debug("Call to acceptOperatorInvitation: {}", invitationTokenDTO);
        return new ResponseEntity<>(operatorInvitedUserInfo, HttpStatus.OK);
    }

    @PutMapping(path = "/accept-authority-and-enable-invited-operator-with-credentials")
    @Operation(summary = "Accept authority and enable operator user from invitation token")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_AUTHORITY_AND_ENABLE_OPERATOR_USER_FROM_INVITATION_WITH_CREDENTIALS_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<OperatorUserDTO> acceptAuthorityAndEnableInvitedUserWithCredentials(
            @RequestBody @Valid @Parameter(description = "The operator user", required = true)
                    OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO) {
        log.debug("Call to acceptAuthorityAndEnableInvitedUserWithCredentials: {}", operatorUserRegistrationWithCredentialsDTO);
        return new ResponseEntity<>(operatorUserActivationService.acceptAuthorityAndEnableInvitedUserWithCredentials(
                operatorUserRegistrationWithCredentialsDTO), HttpStatus.OK);
    }

    @PutMapping(path = "/accept-authority-and-enable-invited-operator-without-credentials")
    @Operation(summary = "Accept authority and enable operator user from invitation token without credentials")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_AUTHORITY_AND_ENABLE_OPERATOR_USER_FROM_INVITATION_WOUT_CREDENTIALS_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<OperatorUserDTO> acceptAuthorityAndEnableInvitedUserWithoutCredentials(
            @RequestBody @Valid @Parameter(description = "The operator user", required = true)
                    OperatorUserRegistrationDTO operatorUserRegistrationDTO) {
        log.debug("Call to acceptAuthorityAndEnableInvitedUserWithoutCredentials: {}", operatorUserRegistrationDTO);
        return new ResponseEntity<>(operatorUserActivationService
                .acceptAuthorityAndEnableInvitedUser(operatorUserRegistrationDTO), HttpStatus.OK);
    }

    @PutMapping(path = "/accept-authority-and-set-credentials-to-user")
    @Operation(summary = "Accept authority and set credentials to user")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.SET_CREDENTIALS_TO_REGISTERED_OPERATOR_USER_FROM_INVITATION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> acceptAuthorityAndSetCredentialsToUser(
            @RequestBody @Valid @Parameter(description = "The operator user credentials", required = true)
                    InvitedUserCredentialsDTO invitedUserCredentialsDTO) {
        log.debug("Call to acceptAuthorityAndSetCredentialsToUser: {}", invitedUserCredentialsDTO);
        operatorUserActivationService.acceptAuthorityAndSetCredentialsToUser(invitedUserCredentialsDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
