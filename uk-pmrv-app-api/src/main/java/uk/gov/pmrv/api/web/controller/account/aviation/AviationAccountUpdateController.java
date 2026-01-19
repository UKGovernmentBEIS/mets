package uk.gov.pmrv.api.web.controller.account.aviation;

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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountUpdateDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateCommencementDateDTO;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpCommandOrchestrator;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.UPDATE_AVIATION_ACCOUNT_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/aviation/accounts/{id}")
@RequiredArgsConstructor
@Validated
@Tag(name = "Aviation account update")
public class AviationAccountUpdateController {

    private final AviationAccountUpdateService aviationAccountUpdateService;
    private final AviationAccountEmpCommandOrchestrator aviationAccountEmpCommandOrchestrator;

    @PostMapping
    @Operation(summary = "Update the aviation account")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = UPDATE_AVIATION_ACCOUNT_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateAviationAccount(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
            @RequestBody @Valid @Parameter(description = "The aviation account fields", required = true) AviationAccountUpdateDTO accountUpdateDTO,
            AppUser user) {
        aviationAccountUpdateService.updateAviationAccount(accountId, accountUpdateDTO, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/commencement-date")
    @Operation(summary = "Update and Validate the commencement date of the account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<Void> updateCommencementDate(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
            @RequestBody @Valid @Parameter(description = "The commencement date", required = true) AccountUpdateCommencementDateDTO commencementDateDTO) {
        aviationAccountEmpCommandOrchestrator.updateAccountCommencementDate(accountId, commencementDateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
