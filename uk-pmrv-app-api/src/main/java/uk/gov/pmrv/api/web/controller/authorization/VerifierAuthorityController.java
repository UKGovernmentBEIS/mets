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
import uk.gov.netz.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityDeletionService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.UsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.service.VerifierUserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.authorization.service.VerifierUserAuthorityUpdateOrchestrator;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/v1.0/verifier-authorities")
@Tag(name = "Verifier authorities")
@RequiredArgsConstructor
public class VerifierAuthorityController {

	private final VerifierUserAuthorityQueryOrchestrator verifierUserAuthorityQueryOrchestrator;
	private final VerifierUserAuthorityUpdateOrchestrator verifierUserAuthorityUpdateOrchestrator;
	private final VerifierAuthorityDeletionService verifierAuthorityDeletionService;

	@GetMapping
	@Operation(summary = "Retrieves the list of verifier authorities related to the verification body to which I have been assigned to")
	@ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsersAuthoritiesInfoDTO.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@AuthorizedRole(roleType = RoleTypeConstants.VERIFIER)
	public ResponseEntity<UsersAuthoritiesInfoDTO> getVerifierAuthorities(@Parameter(hidden = true) AppUser currentUser) {
		return new ResponseEntity<>(
				verifierUserAuthorityQueryOrchestrator.getVerifierUsersAuthoritiesInfo(currentUser),
				HttpStatus.OK);
	}

	@GetMapping("/vb/{id}")
	@Operation(summary = "Retrieves the list of verifier authorities related to the verification body")
	@ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UsersAuthoritiesInfoDTO.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized
	public ResponseEntity<UsersAuthoritiesInfoDTO> getVerifierAuthoritiesByVerificationBodyId(
			@PathVariable("id") @Parameter(description = "The verification body id") Long verificationBodyId) {
		return new ResponseEntity<>(verifierUserAuthorityQueryOrchestrator.getVerifierAuthoritiesByVerificationBodyId(verificationBodyId),
				HttpStatus.OK);
	}

	@PostMapping
	@Operation(summary = "Updates the verifier authorities")
	@ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
	@ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_VERIFIER_AUTHORITY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized
	public ResponseEntity<Void> updateVerifierAuthorities(
			@Parameter(hidden = true) AppUser currentUser,
			@RequestBody @Valid @NotEmpty @Parameter(description = "The verifier authorities to update", required = true)
					List<VerifierAuthorityUpdateDTO> verifierAuthorities){
		Long verificationBodyId = currentUser.getVerificationBodyId();
		verifierUserAuthorityUpdateOrchestrator.updateVerifierAuthorities(verifierAuthorities, verificationBodyId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/vb/{id}")
	@Operation(summary = "Updates the verifier authorities of the specified verification body")
	@ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
	@ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_VERIFIER_AUTHORITY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized
	public ResponseEntity<Void> updateVerifierAuthoritiesByVerificationBodyId(
			@RequestBody @Valid @NotEmpty @Parameter(description = "The verifier authorities to update", required = true)
					List<VerifierAuthorityUpdateDTO> verifierAuthorities,
			@PathVariable("id") @Parameter(description = "The verification body id") Long verificationBodyId){
		verifierUserAuthorityUpdateOrchestrator.updateVerifierAuthorities(verifierAuthorities, verificationBodyId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(path = "/{userId}")
	@Operation(summary = "Delete the verifier user that corresponds to the provided user id")
	@ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
	@ApiResponse(responseCode = "400", description = SwaggerApiInfo.DELETE_VERIFIER_AUTHORITY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized
	public ResponseEntity<Void> deleteVerifierAuthority(
			@Parameter(hidden = true) AppUser authUser,
			@PathVariable("userId") @Parameter(description = "The regulator to be deleted") String userId) {
		verifierAuthorityDeletionService.deleteVerifierAuthority(userId, authUser);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping
	@Operation(summary = "Delete the current verifier user")
	@ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
	@ApiResponse(responseCode = "400", description = SwaggerApiInfo.DELETE_CURRENT_VERIFIER_AUTHORITY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
	@Authorized
	public ResponseEntity<Void> deleteCurrentVerifierAuthority(@Parameter(hidden = true) AppUser authUser) {
		verifierAuthorityDeletionService.deleteVerifierAuthority(authUser.getUserId(), authUser.getVerificationBodyId());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
