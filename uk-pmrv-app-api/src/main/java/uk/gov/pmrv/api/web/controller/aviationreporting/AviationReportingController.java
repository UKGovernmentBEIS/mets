package uk.gov.pmrv.api.web.controller.aviationreporting;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.dto.AviationAerCorsiaEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.dto.AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaAerodromePairsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaInternationalFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaStandardFuelsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.dto.AviationAerEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AerodromePairsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDomesticFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerNonDomesticFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.StandardFuelsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import java.util.List;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@Validated
@RequestMapping(path = "/v1.0/aviation-reporting")
@RequiredArgsConstructor
@Tag(name = "Aviation Reporting")
public class AviationReportingController {

    private final AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;

    private final AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaEmissionsCalculationService;

    @PostMapping("/ukets/total-emissions")
    @Operation(summary = "Calculate total emissions for the scheme year")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AviationAerTotalEmissions.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<AviationAerTotalEmissions> getTotalEmissionsUkEts(
            @RequestBody @Valid @Parameter(description = "DTO containing emission data and saf data is required", required = true)
            AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {
        AviationAerTotalEmissions totalEmissions = aviationAerUkEtsEmissionsCalculationService
                .calculateTotalEmissions(aviationAerEmissionsCalculationDTO);
        return new ResponseEntity<>(totalEmissions, HttpStatus.OK);
    }

    @PostMapping("/ukets/standard-fuels-emissions")
    @Operation(summary = "Retrieves total emissions for standard fuels")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = StandardFuelsTotalEmissions.class)))})
    @ApiResponse(responseCode = "400", description = BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<List<StandardFuelsTotalEmissions>> getStandardFuelsEmissionsUkEts(
            @RequestBody @Valid @Parameter(description = "DTO containing aggregated emissions data and SAF", required = true)
            AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {
        return new ResponseEntity<>(
            aviationAerUkEtsEmissionsCalculationService.calculateStandardFuelsTotalEmissions(aviationAerEmissionsCalculationDTO),
                HttpStatus.OK);
    }

    @PostMapping("/ukets/aerodrome-pairs-emissions")
    @Operation(summary = "Calculate total emissions for aerodrome pairs")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = AerodromePairsTotalEmissions.class))))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<List<AerodromePairsTotalEmissions>> getAerodromePairsEmissionsUkEts(
            @RequestBody @Valid @Parameter(description = "DTO containing aggregated emissions data and SAF", required = true)
            AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {
        List<AerodromePairsTotalEmissions> totalEmissions = aviationAerUkEtsEmissionsCalculationService
                .calculateAerodromePairsTotalEmissions(aviationAerEmissionsCalculationDTO);
        return new ResponseEntity<>(totalEmissions, HttpStatus.OK);
    }

    @PostMapping("/ukets/domestic-flights-emissions")
    @Operation(summary = "Retrieves domestic flights emissions")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AviationAerDomesticFlightsEmissions.class))})
    @ApiResponse(responseCode = "400", description = BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<AviationAerDomesticFlightsEmissions> getDomesticFlightsEmissionsUkEts(
            @RequestBody @Valid @Parameter(description = "DTO containing aggregated emissions data and SAF", required = true)
            AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {
        return new ResponseEntity<>(
            aviationAerUkEtsEmissionsCalculationService.calculateDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO),
                HttpStatus.OK);
    }

    @PostMapping("/ukets/non-domestic-flights-emissions")
    @Operation(summary = "Retrieves domestic flights emissions")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AviationAerNonDomesticFlightsEmissions.class))})
    @ApiResponse(responseCode = "400", description = BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<AviationAerNonDomesticFlightsEmissions> getNonDomesticFlightsEmissionsUkEts(
            @RequestBody @Valid @Parameter(description = "DTO containing aggregated emissions data and SAF", required = true)
            AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO) {
        return new ResponseEntity<>(
            aviationAerUkEtsEmissionsCalculationService.calculateNonDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO),
                HttpStatus.OK);
    }

    @PostMapping("/corsia/total-emissions")
    @Operation(summary = "Calculate total emissions for the scheme year")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AviationAerCorsiaTotalEmissions.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<AviationAerCorsiaTotalEmissions> getTotalEmissionsCorsia(
            @RequestBody @Valid @Parameter(description = "DTO containing emission data and emissions reduction claim data is required", required = true)
            AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO) {
        AviationAerCorsiaTotalEmissions totalEmissions = aviationAerCorsiaEmissionsCalculationService
                .calculateTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO);
        return new ResponseEntity<>(totalEmissions, HttpStatus.OK);
    }

    @PostMapping("/corsia/aerodrome-pairs-emissions")
    @Operation(summary = "Calculate total emissions for aerodrome pairs")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = AviationAerCorsiaAerodromePairsTotalEmissions.class))))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<List<AviationAerCorsiaAerodromePairsTotalEmissions>> getAerodromePairsEmissionsCorsia(
            @RequestBody @Valid @Parameter(description = "DTO containing emission data and emissions reduction claim data is required", required = true)
            AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO) {
        List<AviationAerCorsiaAerodromePairsTotalEmissions> totalEmissions = aviationAerCorsiaEmissionsCalculationService
                .calculateAerodromePairsTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO);
        return new ResponseEntity<>(totalEmissions, HttpStatus.OK);
    }

    @PostMapping("/corsia/standard-fuels-emissions")
    @Operation(summary = "Retrieves total emissions for standard fuels")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = AviationAerCorsiaStandardFuelsTotalEmissions.class)))})
    @ApiResponse(responseCode = "400", description = BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<List<AviationAerCorsiaStandardFuelsTotalEmissions>> getStandardFuelsEmissionsCorsia(
            @RequestBody @Valid @Parameter(description = "DTO containing emission data and emissions reduction claim data is required", required = true)
            AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO) {
        return new ResponseEntity<>(
            aviationAerCorsiaEmissionsCalculationService.calculateStandardFuelsTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO),
            HttpStatus.OK);
    }

    @PostMapping("/corsia/international-flights-emissions")
    @Operation(summary = "Retrieves corsia international flights emissions")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AviationAerCorsiaInternationalFlightsEmissions.class))})
    @ApiResponse(responseCode = "400", description = BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<AviationAerCorsiaInternationalFlightsEmissions> getInternationalFlightsEmissionsCorsia(
        @RequestBody @Valid @Parameter(description = "DTO containing aggregated emissions data for corsia", required = true)
        AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO aviationAerCorsiaInternationalFlightsEmissionsCalculationDTO) {
        return new ResponseEntity<>(
            aviationAerCorsiaEmissionsCalculationService.calculateInternationalFlightsEmissions(aviationAerCorsiaInternationalFlightsEmissionsCalculationDTO),
            HttpStatus.OK);
    }
}
