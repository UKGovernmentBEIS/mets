package uk.gov.pmrv.api.web.controller.reporting;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REPORTING_CALCULATE_EMISSIONS_BAD_REQUEST;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.EmissionsCalculationService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionsCalculationService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionsCalculationService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.pfc.PfcEmissionsCalculationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

@Validated
@RestController
@RequestMapping(path = "/v1.0/reporting")
@RequiredArgsConstructor
@Tag(name = "Reporting")
public class ReportingController {

    private final EmissionsCalculationService emissionsCalculationService;
    private final MeasurementN2OEmissionsCalculationService measurementN2OEmissionsCalculationService;
    private final MeasurementCO2EmissionsCalculationService measurementCO2EmissionsCalculationService;
    private final PfcEmissionsCalculationService pfcEmissionsCalculationService;

    @PostMapping(path = "/calculation/calculate-emissions")
    @Operation(summary = "Calculates emissions using AER calculation approach")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType =
        MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EmissionsCalculationDTO.class))})
    @ApiResponse(responseCode = "400", description = REPORTING_CALCULATE_EMISSIONS_BAD_REQUEST, content =
        {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
        @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType =
        MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType =
        MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = OPERATOR)
    public ResponseEntity<EmissionsCalculationDTO> calculateEmissions(
        @RequestBody @Valid @Parameter(description = "The parameters needed to calculate the emissions", required =
            true)
        EmissionsCalculationParamsDTO emissionsCalculationParams) {
        return new ResponseEntity<>(emissionsCalculationService.calculateEmissions(emissionsCalculationParams),
            HttpStatus.OK);
    }

    @PostMapping(path = "/calculation/pfc/calculate-emissions")
    @Operation(summary = "Calculates emissions using AER calculation approach")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PfcEmissionsCalculationDTO.class))})
    @ApiResponse(responseCode = "400", description = REPORTING_CALCULATE_EMISSIONS_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = OPERATOR)
    public ResponseEntity<PfcEmissionsCalculationDTO> calculatePfcEmissions(
        @RequestBody @Valid @Parameter(description = "The parameters needed to calculate the emissions", required = true)
        PfcEmissionsCalculationParamsDTO emissionsCalculationParams) {
        return new ResponseEntity<>(pfcEmissionsCalculationService.calculateEmissions(emissionsCalculationParams), HttpStatus.OK);
    }

    @PostMapping(path = "/measurement/co2/calculate-emissions")
    @Operation(description = "Calculates emissions using AER calculation approach")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType =
        MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MeasurementEmissionsCalculationDTO.class))})
    @ApiResponse(responseCode = "400", description = REPORTING_CALCULATE_EMISSIONS_BAD_REQUEST, content =
        {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
        @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType =
        MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType =
        MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = OPERATOR)
    public ResponseEntity<MeasurementEmissionsCalculationDTO> calculateMeasurementCO2Emissions(
        @RequestBody @Valid @Parameter(description = "The parameters needed to calculate the emissions", required =
            true)
        MeasurementEmissionsCalculationParamsDTO emissionsCalculationParams) {
        return new ResponseEntity<>(measurementCO2EmissionsCalculationService.calculateEmissions(emissionsCalculationParams), HttpStatus.OK);
    }

    @PostMapping(path = "/measurement/n2o/calculate-emissions")
    @Operation(description = "Calculates emissions using AER calculation approach")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType =
        MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MeasurementEmissionsCalculationDTO.class))})
    @ApiResponse(responseCode = "400", description = REPORTING_CALCULATE_EMISSIONS_BAD_REQUEST, content =
        {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
        @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType =
        MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType =
        MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = OPERATOR)
    public ResponseEntity<MeasurementEmissionsCalculationDTO> calculateMeasurementN2OEmissions(
        @RequestBody @Valid @Parameter(description = "The parameters needed to calculate the emissions", required =
            true)
        MeasurementEmissionsCalculationParamsDTO emissionsCalculationParams) {
        return new ResponseEntity<>(measurementN2OEmissionsCalculationService.calculateEmissions(emissionsCalculationParams), HttpStatus.OK);
    }
}
