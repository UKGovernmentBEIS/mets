package uk.gov.pmrv.api.web.controller.verificationbody;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoResponseDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateStatusDTO;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDeletionService;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyUpdateService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.verificationbody.dto.VerificationBodyCreationDTO;
import uk.gov.pmrv.api.web.orchestrator.verificationbody.service.VerificationBodyAndUserOrchestrator;

import java.util.List;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/verification-bodies")
@Tag(name = "Verification Bodies")
@RequiredArgsConstructor
@Validated
public class VerificationBodyController {

    private final VerificationBodyAndUserOrchestrator verificationBodyAndUserOrchestrator;
    private final VerificationBodyQueryService verificationBodyQueryService;
    private final VerificationBodyUpdateService verificationBodyUpdateService;
    private final VerificationBodyDeletionService verificationBodyDeletionService;

    /**
     * Retrieves all verification bodies.
     *
     * @param appUser {@link AppUser}
     * @return List of {@link VerificationBodyInfoResponseDTO}
     */
    @GetMapping
    @Operation(summary = "Retrieves all verification bodies")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerificationBodyInfoResponseDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<VerificationBodyInfoResponseDTO> getVerificationBodies(@Parameter(hidden = true) AppUser appUser) {
        return new ResponseEntity<>(verificationBodyQueryService.getVerificationBodies(appUser),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get the verification body with the provided id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerificationBodyDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<VerificationBodyDTO> getVerificationBodyById(
            @Parameter(description = "The verification body id") @PathVariable("id") Long verificationBodyId) {
        return new ResponseEntity<>(
                verificationBodyQueryService.getVerificationBodyById(verificationBodyId),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete the verification body with the provided id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> deleteVerificationBodyById(
            @Parameter(description = "The verification body id") @PathVariable("id") Long verificationBodyId) {
        verificationBodyDeletionService.deleteVerificationBodyById(verificationBodyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    @Operation(summary = "Creates a verification body")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerificationBodyInfoDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.CREATE_VERIFICATION_BODY_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<VerificationBodyInfoDTO> createVerificationBody(
            @Parameter(hidden = true) AppUser appUser,
            @RequestBody @Valid @Parameter(description = "The verification body creation dto", required = true)
                    VerificationBodyCreationDTO verificationBodyCreationDTO) {
        VerificationBodyInfoDTO vbInfo =
                verificationBodyAndUserOrchestrator.createVerificationBody(appUser, verificationBodyCreationDTO);
        return new ResponseEntity<>(vbInfo, HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Update the verification body")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_VERIFICATION_BODY_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> updateVerificationBody(
            @RequestBody @Valid @Parameter(description = "The verification body dto to update", required = true)
                    VerificationBodyUpdateDTO verificationBodyUpdateDTO) {
        verificationBodyUpdateService.updateVerificationBody(verificationBodyUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    @Operation(summary = "Update the verification bodies status")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_VERIFICATION_BODY_STATUS_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> updateVerificationBodiesStatus(
            @RequestBody @Valid @Parameter(description = "The verification bodies status dto to update", required = true)
                    List<VerificationBodyUpdateStatusDTO> verificationBodyUpdateStatusList) {
        verificationBodyUpdateService.updateVerificationBodiesStatus(verificationBodyUpdateStatusList);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
