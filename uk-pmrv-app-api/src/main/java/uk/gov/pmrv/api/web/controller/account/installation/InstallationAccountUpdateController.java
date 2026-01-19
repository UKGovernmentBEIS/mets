package uk.gov.pmrv.api.web.controller.account.installation;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateCommencementDateDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateFaStatusDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateInstallationNameDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateRegistryReportingFirstYearDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateSiteNameDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateSopIdDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountCommandOrchestrator;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/installation/accounts/{id}")
@RequiredArgsConstructor
@Validated
@Tag(name = "Installation account update")
public class InstallationAccountUpdateController {

    private final InstallationAccountUpdateService installationAccountUpdateService;
    private final InstallationAccountCommandOrchestrator installationAccountCommandOrchestrator;

    @PostMapping("/site-name")
    @Operation(summary = "Update the site name of the account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateInstallationAccountSiteName(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
            @RequestBody @Valid @Parameter(description = "The site name", required = true) AccountUpdateSiteNameDTO siteNameDTO) {
        installationAccountUpdateService.updateAccountSiteName(accountId, siteNameDTO.getSiteName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/sop-id")
    @Operation(summary = "Update the sop id of the account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateInstallationAccountSopId(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
            @RequestBody @Valid @Parameter(description = "The sop id", required = true) AccountUpdateSopIdDTO sopIdDTO) {
        installationAccountUpdateService.updateAccountSopId(accountId, sopIdDTO.getSopId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/address")
    @Operation(summary = "Updates the address of the account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateInstallationAccountAddress(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
            @RequestBody @Valid @Parameter(description = "The address", required = true) LocationDTO address) {
        installationAccountUpdateService.updateAccountAddress(accountId, address);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/legal-entity")
    @Operation(summary = "Updates the legal entity of the account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateInstallationAccountLegalEntity(
        @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
        @RequestBody @Valid @Parameter(description = "The account legal entity", required = true) LegalEntityDTO legalEntityDTO
    ) {
        installationAccountUpdateService.updateAccountLegalEntity(accountId, legalEntityDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/name")
    @Operation(summary = "Update the installation name of the account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_INSTALLATION_NAME_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateInstallationName(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
            @RequestBody @Valid @Parameter(description = "The installation name", required = true) AccountUpdateInstallationNameDTO installationNameDTO) {
        installationAccountUpdateService.updateInstallationName(accountId, installationNameDTO.getInstallationName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/free-allocation-status")
    @Operation(summary = "Update the installation free allocation status of the account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<Void> updateFaStatus(
        @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
        @RequestBody @Valid @Parameter(description = "Whether the free allocation was received", required = true)
        AccountUpdateFaStatusDTO accountUpdateFaStatusDTO
    ) {
        installationAccountUpdateService.updateFaStatus(accountId, accountUpdateFaStatusDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/commencement-date")
    @Operation(summary = "Update the commencement date of the account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<Void> updateCommencementDate(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
            @RequestBody @Valid @Parameter(description = "The commencement date", required = true) AccountUpdateCommencementDateDTO commencementDateDTO) {
        installationAccountUpdateService.updateCommencementDate(accountId, commencementDateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/registry-reporting-first-year")
    @Operation(summary = "Update the first year of registry reporting obligation field of the account")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK)
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<Void> updateRegistryReportingFirstYear(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId,
            @RequestBody @Valid @Parameter(description = "The first year of registry reporting obligation field", required = true)
            AccountUpdateRegistryReportingFirstYearDTO accountUpdateRegistryReportingFirstYearDTO) {
        installationAccountCommandOrchestrator.updateRegistryReportingFirstYear(accountId, accountUpdateRegistryReportingFirstYearDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
