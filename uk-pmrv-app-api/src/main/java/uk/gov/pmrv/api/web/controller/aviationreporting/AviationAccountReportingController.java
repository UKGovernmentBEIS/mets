package uk.gov.pmrv.api.web.controller.aviationreporting;

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
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationReportableEmissionsDTO;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationReportingYearsDTO;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.time.Year;
import java.util.Map;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@Validated
@RestController
@RequestMapping(path = "/v1.0/aviation-reporting/account")
@RequiredArgsConstructor
@Tag(name = "Aviation Account Reporting")
public class AviationAccountReportingController {

    private final AviationReportableEmissionsService aviationReportableEmissionsService;

    @PostMapping("/{id}/emissions")
    @Operation(summary = "Retrieves the year's emissions for an aviation account")
    @ApiResponse(responseCode = "200", description = OK, useReturnTypeSchema = true)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Map<Year, AviationReportableEmissionsDTO>> getAviationAccountReportableEmissions(
        @Parameter(description = "The account id") @PathVariable("id") Long accountId,
        @RequestBody @Valid @Parameter(description = "The years for which reportable emissions are required", required = true)
        AviationReportingYearsDTO reportingYearsDTO) {
        return new ResponseEntity<>(
            aviationReportableEmissionsService.getReportableEmissions(accountId, reportingYearsDTO.getYears()),
            HttpStatus.OK);
    }
}
