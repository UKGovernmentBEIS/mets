package uk.gov.pmrv.api.web.controller.reference;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.referencedata.domain.dto.ReferenceDataDTO;
import uk.gov.netz.api.referencedata.domain.enumeration.ReferenceDataType;
import uk.gov.netz.api.referencedata.service.ReferenceDataService;
import uk.gov.netz.api.referencedata.service.ReferenceDataTypeServiceEnum;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

/**
 * Controller for reference data that are needed by the UI.
 */
@RestController
@RequestMapping(path = "/v1.0/data")
@Tag(name = "Reference Data")
@SecurityRequirements
@RequiredArgsConstructor
public class ReferenceDataController {

    private final ApplicationContext context;

    /**
     * Returns the reference data by type.
     *
     * @param types List of {@link ReferenceDataType}.
     * @return The map of reference data
     */
    @GetMapping
    @Operation(summary = "Retrieves reference data by type")
    @ApiResponse(responseCode = "200", description = OK, useReturnTypeSchema = true)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Map<ReferenceDataType, List<ReferenceDataDTO>>> getReferenceData(@Parameter(description = "The reference data types.")
                                                                                           @RequestParam List<ReferenceDataType> types) {

        Map<ReferenceDataType, List<ReferenceDataDTO>> referenceDataTypeListMap = new EnumMap<>(ReferenceDataType.class);

        types.forEach(referenceDataType -> {
            ReferenceDataTypeServiceEnum typeService = ReferenceDataTypeServiceEnum.resolve(referenceDataType);
            if(typeService == null) {
                return;
            }
            ReferenceDataService referenceDataService = context.getBean(typeService.getReferenceDataService());
            referenceDataTypeListMap.put(referenceDataType, typeService.getReferenceDataMapper().toDTOs(referenceDataService.getReferenceData()));
        });

        return new ResponseEntity<>(referenceDataTypeListMap, HttpStatus.OK);
    }
}
