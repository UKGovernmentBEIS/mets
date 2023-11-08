package uk.gov.pmrv.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityInfoDTO;
import uk.gov.pmrv.api.account.service.LegalEntityService;
import uk.gov.pmrv.api.account.service.LegalEntityValidationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import java.util.List;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.ACCOUNT_LEGAL_ENTITY_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

/**
 * Controller for legal entities.
 */
@RestController
@RequestMapping(path = "/v1.0/legal-entities")
@Tag(name = "Legal Entities")
@RequiredArgsConstructor
public class LegalEntityController {

    private final LegalEntityService legalEntityService;
    private final LegalEntityValidationService legalEntityValidationService;

    /**
     * Returns all legal entities available for the current user.
     *
     * @return List of {@link LegalEntityDTO}
     */
    @GetMapping
    @Operation(summary = "Retrieves all legal entities associated with the user")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = LegalEntityInfoDTO.class))))
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<List<LegalEntityInfoDTO>> getCurrentUserLegalEntities(@Parameter(hidden = true) PmrvUser pmrvUser) {
        return new ResponseEntity<>(legalEntityService.getUserLegalEntities(pmrvUser),
                HttpStatus.OK);
    }

    /**
     * Returns legal entity with specified id that is associated with the user.
     *
     * @return List of {@link LegalEntityDTO}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Returns legal entity with the specified id that is associated with the user")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LegalEntityDTO.class))})
    @ApiResponse(responseCode = "400", description = ACCOUNT_LEGAL_ENTITY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR})
    public ResponseEntity<LegalEntityDTO> getLegalEntityById(@Parameter(hidden = true) PmrvUser pmrvUser,
                                                             @Parameter(description = "The legal entity id") @PathVariable("id") Long id) {
        return new ResponseEntity<>(legalEntityService.getUserLegalEntityDTOById(id, pmrvUser),
                HttpStatus.OK);
    }

    @GetMapping("/name")
    @Operation(summary = "Checks if legal entity name exists")
    @ApiResponse(responseCode = "200", description = OK)
    @ApiResponse(responseCode = "400", description = ACCOUNT_LEGAL_ENTITY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Boolean> isExistingLegalEntityName(@Parameter(hidden = true) PmrvUser pmrvUser,
                                                             @RequestParam("name") @Parameter(name = "name", description = "The legal entity name to check")  String legalEntityName) {
        boolean exists = legalEntityValidationService.isExistingActiveLegalEntityName(legalEntityName, pmrvUser);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}
