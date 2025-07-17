package uk.gov.pmrv.api.web.controller.reporting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.dto.ChargingZoneDTO;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.dto.InventoryDataYearExistenceDTO;
import uk.gov.pmrv.api.reporting.domain.dto.NationalInventoryDataDTO;
import uk.gov.pmrv.api.reporting.domain.dto.RegionalInventoryEmissionCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.SourceStreamCalculationParametersInfo;
import uk.gov.pmrv.api.reporting.service.ChargingZoneService;
import uk.gov.pmrv.api.reporting.service.NationalInventoryDataService;
import uk.gov.pmrv.api.reporting.service.RegionalInventoryDataService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.SourceStreamCalculationParametersInfoService;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.time.Year;
import java.util.List;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/reporting-data")
@RequiredArgsConstructor
@Tag(name = "Reporting Data")
public class ReportingDataController {

    private final ChargingZoneService chargingZoneService;
    private final RegionalInventoryDataService regionalInventoryDataService;
    private final NationalInventoryDataService nationalInventoryDataService;
    private final SourceStreamCalculationParametersInfoService sourceStreamCalculationParametersInfoService;

    @GetMapping
    @Operation(summary = "Returns reporting year inventory data existence")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InventoryDataYearExistenceDTO.class)))
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<InventoryDataYearExistenceDTO> getInventoryDataExistenceByYear(
            @RequestParam(value = "year") @Parameter(name = "year", description = "The reporting year") @NotNull Year year,
            @RequestParam(value = "method") @Parameter(name = "method", description = "The calculation method type") @NotNull InventoryCalculationMethodType method) {
        InventoryDataYearExistenceDTO inventoryDataYearExistenceDTO = switch (method) {
            case NATIONAL -> nationalInventoryDataService.getInventoryDataExistenceByYear(year);
            case REGIONAL -> regionalInventoryDataService.getInventoryDataExistenceByYear(year);
        };

        return new ResponseEntity<>(inventoryDataYearExistenceDTO, HttpStatus.OK);
    }

    @GetMapping("/charging-zones")
    @Operation(summary = "Returns charging zones associated with the provided post code")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ChargingZoneDTO.class))))
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<List<ChargingZoneDTO>> getChargingZonesByPostCode(
            @RequestParam(value = "code") @Parameter(name = "code", description = "The post code") @NotBlank String code) {
        return new ResponseEntity<>(chargingZoneService.getChargingZonesByPostCode(code), HttpStatus.OK);
    }

    @GetMapping("/regional-inventory-data")
    @Operation(summary = "Returns regional emission calculation parameters for the provided year and charging zone")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RegionalInventoryEmissionCalculationParamsDTO.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<RegionalInventoryEmissionCalculationParamsDTO> getRegionalInventoryEmissionCalculationParams(
            @RequestParam(value = "year") @Parameter(name = "year", description = "The reporting year") @NotNull Year year,
            @RequestParam(value = "chargingZoneCode") @Parameter(name = "chargingZoneCode", description = "The charging zone code") @NotBlank String chargingZoneCode) {
        RegionalInventoryEmissionCalculationParamsDTO regionalInventoryEmissionCalculationParams =
                regionalInventoryDataService.getRegionalInventoryEmissionCalculationParams(year, chargingZoneCode)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return new ResponseEntity<>(regionalInventoryEmissionCalculationParams, HttpStatus.OK);
    }

    @GetMapping("/national-inventory-data")
    @Operation(summary = "Returns national inventory data for the provided year")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NationalInventoryDataDTO.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<NationalInventoryDataDTO> getNationalInventoryData(
            @RequestParam(value = "year") @Parameter(name = "year", description = "The reporting year") @NotNull Year year) {
        return new ResponseEntity<>(nationalInventoryDataService.getNationalInventoryDataByReportingYear(year), HttpStatus.OK);
    }

    @GetMapping("/calculation-parameters-info")
    @Operation(summary = "Returns calculation parameters information by source stream type")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SourceStreamCalculationParametersInfo.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<SourceStreamCalculationParametersInfo> getSourceStreamEmissionsCalculationInfoByType(
            @RequestParam(value = "sourceStreamType") @Parameter(name = "sourceStreamType", description = "The source stream type") @NotNull SourceStreamType sourceStreamType) {
        return new ResponseEntity<>(
                sourceStreamCalculationParametersInfoService.getCalculationParametersInfoBySourceStreamType(sourceStreamType),
                HttpStatus.OK
        );
    }
}
