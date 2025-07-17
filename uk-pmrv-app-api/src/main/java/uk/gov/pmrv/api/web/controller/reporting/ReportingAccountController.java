package uk.gov.pmrv.api.web.controller.reporting;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.reporting.domain.dto.ReportingYearsDTO;
import uk.gov.pmrv.api.reporting.service.ReportableEmissionsService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@Validated
@RestController
@RequestMapping(path = "/v1.0/reporting/account")
@RequiredArgsConstructor
@Tag(name = "Account Reporting")
public class ReportingAccountController {

    private final ReportableEmissionsService reportableEmissionsService;

    @PostMapping("/{id}/emissions")
    @Operation(summary = "Retrieves the year's emissions for account")
    @ApiResponse(responseCode = "200", description = OK, useReturnTypeSchema = true)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Map<Year, BigDecimal>> getReportableEmissions(
            @Parameter(description = "The account id") @PathVariable("id") Long accountId,
            @RequestBody @Valid @Parameter(description = "The years for which reportable emissions are required", required = true)
            ReportingYearsDTO reportingYearsDTO) {
        return new ResponseEntity<>(reportableEmissionsService.getReportableEmissions(accountId, reportingYearsDTO.getYears()), HttpStatus.OK);
    }
}
