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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.pmrv.api.user.core.domain.dto.OneTimePasswordDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.core.service.UserSecuritySetupService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

@RestController
@RequestMapping(path = "/v1.0/users/security-setup")
@Tag(name = "Users Security Setup")
@RequiredArgsConstructor
public class UserSecuritySetupController {

    private final UserSecuritySetupService userSecuritySetupService;
    private final AppSecurityComponent appSecurityComponent;

    @PostMapping(path = "/2fa/request-change")
    @Operation(summary = "Requests the update of the two factor authentication")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.REQUEST_TO_CHANGE_2FA_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> requestTwoFactorAuthChange
            (@Parameter(hidden = true) AppUser currentUser,
             @RequestBody @Valid @Parameter(description = "The one time authenticator code", required = true)
                     OneTimePasswordDTO oneTimePasswordDTO) {
        userSecuritySetupService.requestTwoFactorAuthChange(currentUser, appSecurityComponent.getAccessToken(),
                oneTimePasswordDTO.getPassword());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/2fa/delete")
    @Operation(summary = "Delete the two factor authentication")
    @SecurityRequirements
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.REMOVE_2FA_BAD_REQUEST,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> deleteOtpCredentials(
            @RequestBody @Valid @Parameter(description = "The change 2FA token", required = true) TokenDTO tokenDTO) {
        userSecuritySetupService.deleteOtpCredentials(tokenDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
