package uk.gov.pmrv.api.web.controller.aviationreporting;

import io.swagger.v3.oas.annotations.Operation;
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
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsYearDTO;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationRptAirportsService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.List;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@Validated
@RequestMapping(path = "/v1.0/aviation-reporting-data")
@RequiredArgsConstructor
@Tag(name = "Aviation Reported Airports")
public class AviationReportingDataController {

    private final AviationRptAirportsService aviationRptAirportsService;

    @PostMapping("/airports")
    @Operation(summary = "Retrieves information for given list of ICAO codes")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = AviationRptAirportsDTO.class)))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<List<AviationRptAirportsDTO>> getReportedAirports(@RequestBody @Valid AviationRptAirportsYearDTO airportsYearDTO) {
        return new ResponseEntity<>(
            aviationRptAirportsService.getAirportsByIcaoCodesAndYear(airportsYearDTO.getIcaos(), airportsYearDTO.getYear()),
            HttpStatus.OK);
    }
}
