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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserManagementService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

/**
 * Controller for managing verifier users.
 */
@RestController
@RequestMapping(path = "/v1.0/verifier-users")
@Tag(name = "Verifier Users")
@RequiredArgsConstructor
public class VerifierUserManagementController {

    private final VerifierUserManagementService verifierUserManagementService;

    /**
     * Retrieves info of verifier user by user id.
     *
     * @param userId Keycloak user id
     * @return {@link VerifierUserDTO}
     */
    @GetMapping(path = "/{userId}")
    @Operation(summary = "Retrieves the user of type VERIFIER for the given user id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerifierUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.GET_VERIFIER_USER_BY_ID_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<VerifierUserDTO> getVerifierUserById(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("userId") @Parameter(description = "The verifier user id") String userId) {
        return new ResponseEntity<>(verifierUserManagementService.getVerifierUserById(appUser, userId),
                HttpStatus.OK);
    }

    /**
     * Updates verifier user by user id.
     *
     * @param appUser {@link AppUser}
     * @param verifierUserDTO {@link VerifierUserDTO}
     * @return {@link VerifierUserDTO}
     */
    @PatchMapping(path = "/{userId}")
    @Operation(summary = "Updates verifier user by user id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerifierUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_VERIFIER_USER_BY_ID_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<VerifierUserDTO> updateVerifierUserById(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("userId") @Parameter(description = "The verifier user id") String userId,
            @RequestBody @Valid @Parameter(description = "The modified verifier user", required = true) VerifierUserDTO verifierUserDTO) {
        verifierUserManagementService.updateVerifierUserById(appUser, userId, verifierUserDTO);
        return new ResponseEntity<>(verifierUserDTO, HttpStatus.OK);
    }

    /**
     * Updates logged in verifier user.
     *
     * @param verifierUserDTO {@link VerifierUserDTO}
     * @return {@link VerifierUserDTO}
     */
    @PatchMapping(path = "/verifier")
    @Operation(summary = "Updates logged in verifier user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerifierUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = RoleTypeConstants.VERIFIER)
    public ResponseEntity<VerifierUserDTO> updateCurrentVerifierUser(
            @RequestBody @Valid @Parameter(description = "The modified verifier user", required = true) VerifierUserDTO verifierUserDTO) {
        verifierUserManagementService.updateCurrentVerifierUser(verifierUserDTO);
        return new ResponseEntity<>(verifierUserDTO, HttpStatus.OK);
    }
    
    /**
     * Resets the 2FA device for a verifier user by user id.
     *
     * @param appUser {@link AppUser}
     * @param userId Keycloak user id
     */
    @PatchMapping(path = "/{userId}/reset-2fa")
    @Operation(summary = "Resets the 2FA device for a verifier user by user id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerifierUserDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_VERIFIER_USER_BY_ID_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> resetVerifier2Fa(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("userId") @Parameter(description = "The verifier user id") String userId) {
        verifierUserManagementService.resetVerifier2Fa(appUser, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
