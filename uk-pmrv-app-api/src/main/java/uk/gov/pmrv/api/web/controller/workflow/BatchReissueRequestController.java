package uk.gov.pmrv.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.workflow.dto.BatchReissuesResponseDTO;
import uk.gov.pmrv.api.web.orchestrator.workflow.service.BatchReissueRequestsAndInitiatePermissionOrchestrator;

@Validated
@RestController
@RequestMapping(path = "/v1.0/{accountType}/batch-reissue-requests")
@Tag(name = "Requests")
@RequiredArgsConstructor
public class BatchReissueRequestController {

	private final BatchReissueRequestsAndInitiatePermissionOrchestrator orchestrator;
	
	@GetMapping
    @Operation(summary = "Get the batch reissue requests")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BatchReissuesResponseDTO.class))})
	@ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {RoleTypeConstants.REGULATOR})
	public ResponseEntity<BatchReissuesResponseDTO> getBatchReissueRequests(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType,
            @RequestParam(value = "page") @NotNull @Parameter(name="page", description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam(value = "size") @NotNull @Parameter(name="size", description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")  Long pageSize
    ) {
		return new ResponseEntity<>(orchestrator.findBatchReissueRequests(appUser, accountType,
				PagingRequest.builder().pageNumber(page).pageSize(pageSize).build()), HttpStatus.OK);
    }
}
