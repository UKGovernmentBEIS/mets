package uk.gov.pmrv.api.web.controller.account.aviation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryListResponse;
import uk.gov.pmrv.api.account.aviation.service.reportingstatus.AviationAccountReportingStatusHistoryCreationService;
import uk.gov.pmrv.api.account.aviation.service.reportingstatus.AviationAccountReportingStatusHistoryQueryService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.SUBMIT_REPORTING_STATUS_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/aviation/accounts/reporting-status-history")
@RequiredArgsConstructor
@Validated
@Tag(name = "Aviation account reporting status")
public class AviationAccountReportingStatusHistoryController {

    private final AviationAccountReportingStatusHistoryQueryService queryService;

    private final AviationAccountReportingStatusHistoryCreationService creationService;

    @GetMapping("/history")
    @Operation(summary = "Get reporting status history list for an aviation account")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AviationAccountReportingStatusHistoryListResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AviationAccountReportingStatusHistoryListResponse> getReportingStatusHistory(
            @RequestParam(value = "accountId") @Parameter(description = "The account Id", required = true) Long accountId,
            @RequestParam("page") @Parameter(description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") @NotNull(message = "{parameter.page.typeMismatch}") Integer page,
            @RequestParam("size") @Parameter(description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}") @NotNull(message = "{parameter.pageSize.typeMismatch}") Integer pageSize) {
        return new ResponseEntity<>(queryService.getReportingStatusHistoryListResponse(accountId, page, pageSize), HttpStatus.OK);
    }

    @PostMapping("/{accountId}")
    @Operation(summary = "Submits an aviation account reporting status")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SUBMIT_REPORTING_STATUS_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> submitReportingStatus(
            PmrvUser pmrvUser,
            @PathVariable("accountId") @Parameter(description = "The account id", required = true) Long accountId,
            @RequestBody @Valid @Parameter(description = "The aviation account reporting status submit dto", required = true)
            AviationAccountReportingStatusHistoryCreationDTO reportingStatusCreationDTO) {
    	creationService.submitReportingStatus(accountId, reportingStatusCreationDTO, pmrvUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
