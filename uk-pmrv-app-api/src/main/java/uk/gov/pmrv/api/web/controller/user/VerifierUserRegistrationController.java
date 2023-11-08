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
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserAcceptInvitationService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.ENABLE_VERIFIER_USER_FROM_INVITATION_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;

@RestController
@RequestMapping(path = "/v1.0/verifier-users/registration")
@Tag(name = "Verifier users registration")
@SecurityRequirements
@RequiredArgsConstructor
public class VerifierUserRegistrationController {

    private final VerifierUserInvitationService verifierUserInvitationService;
    private final VerifierUserAcceptInvitationService verifierUserAcceptInvitationService;

    @PostMapping(path = "/accept-invitation")
    @Operation(summary = "Accept invitation for verifier user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvitedUserInfoDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_VERIFIER_USER_INVITATION_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<InvitedUserInfoDTO> acceptVerifierInvitation(
            @RequestBody @Valid @Parameter(description = "The invitation token", required = true) TokenDTO invitationTokenDTO) {
        return new ResponseEntity<>(verifierUserInvitationService.acceptInvitation(invitationTokenDTO.getToken()), HttpStatus.OK);
    }

    @PutMapping(path = "/enable-from-invitation")
    @Operation(summary = "Enable a new verifier user from invitation")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = ENABLE_VERIFIER_USER_FROM_INVITATION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> acceptAndEnableVerifierInvitedUser(
            @RequestBody @Valid @Parameter(description = "The verifier user", required = true)
                    InvitedUserEnableDTO invitedUserEnableDTO) {
        verifierUserAcceptInvitationService.acceptAndEnableVerifierInvitedUser(invitedUserEnableDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
