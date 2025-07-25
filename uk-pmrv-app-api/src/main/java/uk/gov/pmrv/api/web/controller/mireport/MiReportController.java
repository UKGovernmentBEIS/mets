package uk.gov.pmrv.api.web.controller.mireport;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.netz.api.mireport.domain.MiReportParams;
import uk.gov.netz.api.mireport.domain.MiReportResult;
import uk.gov.netz.api.mireport.domain.MiReportSearchResult;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.aviation.outstandingrequesttasks.AviationOutstandingRequestTasksReportService;
import uk.gov.pmrv.api.mireport.common.PmrvMiReportService;
import uk.gov.pmrv.api.mireport.installation.outstandingrequesttasks.InstallationOutstandingRequestTasksReportService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/{accountType}/mireports")
@RequiredArgsConstructor
@Tag(name = "MiReports")
@Validated
public class MiReportController {

    private final PmrvMiReportService pmrvMiReportService;
    private final AviationOutstandingRequestTasksReportService aviationOutstandingRequestTasksReportService;
    private final InstallationOutstandingRequestTasksReportService installationOutstandingRequestTasksReportService;

    @GetMapping("types")
    @Operation(summary = "Retrieves the mi report types for current user")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MiReportSearchResult.class))))
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})

    public ResponseEntity<List<MiReportSearchResult>> getCurrentUserMiReports(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType
    ) {
        List<MiReportSearchResult> results =
            pmrvMiReportService.findByCompetentAuthorityAndAccountType(appUser.getCompetentAuthority(), accountType);
        return ResponseEntity.ok(results);
    }

    @PostMapping
    @Operation(summary = "Generates the report identified by the provided report type")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportResult.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportResult> generateReport(@Parameter(hidden = true) AppUser appUser,
                                                         @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType,
                                                         @RequestBody
                                                         @Parameter(description = "The parameters based on which the report will be generated", required = true)
                                                         @Valid
                                                         @SpELExpression(expression = "{(#reportType ne 'CUSTOM')}", message = "mireport.type.notSupported") MiReportParams reportParams) {
        MiReportResult reportResult = pmrvMiReportService.generateReport(appUser.getCompetentAuthority(), accountType, reportParams);
        return ResponseEntity.ok(reportResult);
    }

    @PostMapping("/custom")
    @Operation(summary = "Generates custom report")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportResult.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportResult> generateCustomReport(@Parameter(hidden = true) AppUser appUser,
                                                               @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType,
                                                               @RequestBody
                                                               @Parameter(description = "The parameters based on which the report will be generated", required = true)
                                                               @Valid
                                                               @SpELExpression(expression = "{(#reportType eq 'CUSTOM')}", message = "mireport.type.notSupported")
                                                                       MiReportParams reportParams) {
        MiReportResult reportResult = pmrvMiReportService.generateReport(appUser.getCompetentAuthority(), accountType, reportParams);
        return ResponseEntity.ok(reportResult);
    }

    @GetMapping("/request-task-types")
    @Operation(summary = "Get regulator related request task types")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = RequestTaskType.class))))
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {RoleTypeConstants.REGULATOR})
    public ResponseEntity<Set<RequestTaskType>> getRegulatorRequestTaskTypes(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType) {
        Set<RequestTaskType> requestTaskTypes = new HashSet<>();

        if (AccountType.INSTALLATION == accountType) {
            requestTaskTypes =
                    installationOutstandingRequestTasksReportService.getRequestTaskTypesByRoleTypeAndAccountType(appUser.getRoleType(), accountType);
        } else if (AccountType.AVIATION == accountType) {
            requestTaskTypes =
                    aviationOutstandingRequestTasksReportService.getRequestTaskTypesByRoleTypeAndAccountType(appUser.getRoleType(), accountType);
        }
        return new ResponseEntity<>(requestTaskTypes, HttpStatus.OK);
    }
}
