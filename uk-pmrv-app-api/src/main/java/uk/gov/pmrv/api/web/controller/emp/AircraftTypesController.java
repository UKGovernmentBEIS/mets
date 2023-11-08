package uk.gov.pmrv.api.web.controller.emp;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchCriteria;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.AircraftTypeSearchResults;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.AircraftTypeQueryService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;

@Validated
@RestController
@RequestMapping(path = "/v1.0/aircraft-types")
@Tag(name = "Aircraft Types")
@RequiredArgsConstructor
public class AircraftTypesController {

    private final AircraftTypeQueryService aircraftTypeQueryService;

    @PostMapping
    @Operation(summary = "Retrieves the aircraft types for the given search criteria")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AircraftTypeSearchResults.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})

    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<AircraftTypeSearchResults> getAircraftTypes(
        @RequestBody @Valid @Parameter(description = "The search criteria", required = true) AircraftTypeSearchCriteria searchCriteria) {
        return new ResponseEntity<>(aircraftTypeQueryService.getAircraftTypesBySearchCriteria(searchCriteria), HttpStatus.OK);
    }
}
