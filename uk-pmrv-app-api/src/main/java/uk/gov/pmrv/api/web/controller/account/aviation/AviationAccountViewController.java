package uk.gov.pmrv.api.web.controller.account.aviation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpQueryOrchestrator;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

/**
 * Rest controller that provides info about an aviation account. <br/>
 */
@RestController
@RequestMapping(path = "/v1.0/aviation/account")
@RequiredArgsConstructor
@Tag(name = "Aviation account view")
public class AviationAccountViewController {

    private final AviationAccountEmpQueryOrchestrator orchestrator;

    @GetMapping("/{id}")
    @Operation(summary = "Get the aviation account with the provided id")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AviationAccountEmpDTO.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AviationAccountEmpDTO> getAviationAccountById(
            @Parameter(hidden = true) AppUser appUser,
            @Parameter(description = "The account id") @PathVariable("id") Long accountId) {
        return new ResponseEntity<>(orchestrator.getAviationAccountWithEMP(accountId, appUser), HttpStatus.OK);
    }

    @GetMapping("/{id}/header-info")
    @Operation(summary = "Get the account header info for the provided aviation account")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AviationAccountHeaderInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AviationAccountHeaderInfoDTO> getAviationAccountHeaderInfoById(
        @Parameter(description = "The account id") @PathVariable("id") Long accountId) {
        return new ResponseEntity<>(orchestrator.getAccountHeaderInfo(accountId), HttpStatus.OK);
    }
}
