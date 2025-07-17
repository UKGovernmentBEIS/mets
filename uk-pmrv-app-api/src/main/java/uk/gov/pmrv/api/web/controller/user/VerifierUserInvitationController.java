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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

/**
 * Controller for adding verifier users.
 */
@RestController
@RequestMapping(path = "/v1.0/verifier-users/invite")
@Tag(name = "Verifier Users Invitation")
@RequiredArgsConstructor
public class VerifierUserInvitationController {

    private final VerifierUserInvitationService verifierUserInvitationService;

    @PostMapping
    @Operation(summary = "Invite new verifier user to the verification body")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.INVITE_VERIFIER_USER_TO_VB_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> inviteVerifierUser(
            @Parameter(hidden = true) AppUser currentUser,
            @RequestBody @Valid @Parameter(description = "The verifier user information", required = true)
                    VerifierUserInvitationDTO verifierUserInvitation) {
        verifierUserInvitationService.inviteVerifierUser(currentUser, verifierUserInvitation);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/vb/{id}")
    @Operation(summary = "Invite new admin verifier user to the verification body")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.INVITE_ADMIN_VERIFIER_TO_VB_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> inviteVerifierAdminUserByVerificationBodyId(
            @PathVariable("id") @Parameter(description = "The verification body id") Long verificationBodyId,
            @Parameter(hidden = true) AppUser currentUser,
            @RequestBody @Valid @Parameter(description = "The verifier user information", required = true)
                    AdminVerifierUserInvitationDTO adminVerifierUserInvitationDTO) {
        verifierUserInvitationService.inviteVerifierAdminUser(currentUser, adminVerifierUserInvitationDTO, verificationBodyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
