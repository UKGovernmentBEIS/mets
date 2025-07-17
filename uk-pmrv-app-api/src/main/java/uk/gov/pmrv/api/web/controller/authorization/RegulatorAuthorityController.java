package uk.gov.pmrv.api.web.controller.authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorUserUpdateStatusDTO;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityDeletionService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.RegulatorUsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.service.RegulatorUserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.authorization.service.RegulatorUserAuthorityUpdateOrchestrator;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/v1.0/regulator-authorities")
@Tag(name = "Regulator Authorities")
@RequiredArgsConstructor
public class RegulatorAuthorityController {

    private final RegulatorAuthorityDeletionService regulatorAuthorityDeletionService;
    private final RegulatorUserAuthorityQueryOrchestrator regulatorUserAuthorityQueryOrchestrator;
    private final RegulatorUserAuthorityUpdateOrchestrator regulatorUserAuthorityUpdateOrchestrator;

    @PostMapping
    @Operation(summary = "Updates regulator users status")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> updateCompetentAuthorityRegulatorUsersStatus(
            @Parameter(hidden = true) AppUser appUser,
            @RequestBody @Valid @NotEmpty @Parameter(description = "The regulator users to update", required = true)
            List<RegulatorUserUpdateStatusDTO> regulatorUsers) {
        regulatorUserAuthorityUpdateOrchestrator.updateRegulatorUsersStatus(regulatorUsers, appUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/{userId}")
    @Operation(summary = "Delete the regulator user that corresponds to the provided user id")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> deleteRegulatorUserByCompetentAuthority(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("userId") @Parameter(description = "The regulator to be deleted") String userId) {
        regulatorAuthorityDeletionService.deleteRegulatorUser(userId, appUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Operation(summary = "Delete the current regulator user")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> deleteCurrentRegulatorUserByCompetentAuthority(@Parameter(hidden = true) AppUser currentUser) {
        regulatorAuthorityDeletionService.deleteCurrentRegulatorUser(currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "Retrieves the users of type REGULATOR")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RegulatorUsersAuthoritiesInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = RoleTypeConstants.REGULATOR)
    public ResponseEntity<RegulatorUsersAuthoritiesInfoDTO> getCaRegulators(@Parameter(hidden = true) AppUser appUser) {

        return new ResponseEntity<>(regulatorUserAuthorityQueryOrchestrator.getCaUsersAuthoritiesInfo(appUser),
                HttpStatus.OK);
    }
}
