package uk.gov.pmrv.api.web.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.user.core.domain.dto.EmailDTO;
import uk.gov.pmrv.api.user.core.domain.dto.ResetPasswordDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.core.service.UserResetPasswordService;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_RESET_PASSWORD_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.RESET_PASSWORD_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.TOKEN_VERIFICATION_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/users/forgot-password")
@Tag(name = "Forgot Password")
@RequiredArgsConstructor
@Log4j2
public class ForgotPasswordController {
	
	private final UserResetPasswordService userResetPasswordService;

	/**
     * Sends a verification email to the provided email.
     *
     * @param emailDTO {@link EmailDTO}
     */
    @PostMapping(path = "/reset-password-email")
    @Operation(summary = "Sends a reset password email")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = REQUEST_RESET_PASSWORD_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> sendResetPasswordEmail(
        @RequestBody @Valid @Parameter(description = "The user email", required = true) EmailDTO emailDTO) {
        log.debug("Call to sendResetPasswordEmail: {}", emailDTO);
        userResetPasswordService.sendResetPasswordEmail(emailDTO.getEmail());
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
    @ApiResponse(responseCode = "400", description = TOKEN_VERIFICATION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<EmailDTO> verifyToken(
    		@RequestBody @Valid @Parameter(description = "The verification token", required = true) TokenDTO tokenDTO) {
        String email = userResetPasswordService.verifyToken(tokenDTO.getToken());
        return new ResponseEntity<>(new EmailDTO(email), HttpStatus.OK);
    }
    
    /**
     * Resets the user password.
     *
     * @param resetPasswordDTO {@link ResetPasswordDTO}
     */
    @PostMapping(path = "/reset-password")
    @Operation(summary = "Resets user's password")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = RESET_PASSWORD_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> resetPassword(
    		@RequestBody @Valid @Parameter(description = "The new password", required = true) 
    		ResetPasswordDTO resetPasswordDTO) {
    	userResetPasswordService.resetPassword(resetPasswordDTO);
    	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
