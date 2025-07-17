package uk.gov.pmrv.api.web.controller.account.aviation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResults;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountCreationService;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.CREATE_AVIATION_ACCOUNT_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@Validated
@RequestMapping(path = "/v1.0/aviation/accounts")
@RequiredArgsConstructor
@Tag(name = "Aviation accounts")
public class AviationAccountController {

    private final AviationAccountCreationService aviationAccountCreationService;
    private final AviationAccountQueryService aviationAccountQueryService;

    @PostMapping
    @Operation(summary = "Creates an aviation account")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = CREATE_AVIATION_ACCOUNT_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<Void> createAviationAccount(
            @Parameter(hidden = true) AppUser appUser,
            @RequestBody @Valid @Parameter(description = "The aviation account creation dto", required = true)
                    AviationAccountCreationDTO aviationAccountCreationDTO) {
        aviationAccountCreationService.createAccount(aviationAccountCreationDTO, appUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/name")
    @Operation(summary = "Checks if account name exists")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Boolean.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<Boolean> isExistingAccountName(
            @Parameter(hidden = true) AppUser appUser,
            @RequestParam("name") @NotBlank @Parameter(description = "The account name", required = true) String accountName,
            @RequestParam("scheme") @NotNull @Parameter(description = "The emission trading scheme", required = true) EmissionTradingScheme emissionTradingScheme,
            @RequestParam(value = "accountId", required = false) @Parameter(description = "The account Id") Long accountId) {
        boolean exists =
                aviationAccountQueryService.isExistingAccountName(accountName, appUser.getCompetentAuthority(), emissionTradingScheme, accountId);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @GetMapping("/crco-code")
    @Operation(summary = "Checks if central route charges office code exists")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Boolean.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<Boolean> isExistingCrcoCode(
            @Parameter(hidden = true)AppUser appUser,
            @RequestParam("code") @NotBlank @Parameter(description = "The central route charges office code", required = true) String crcoCode,
            @RequestParam("scheme") @NotNull @Parameter(description = "The emission trading scheme", required = true) EmissionTradingScheme emissionTradingScheme,
            @RequestParam(value = "accountId", required = false) @Parameter(description = "The account Id") Long accountId) {
        boolean exists =
                aviationAccountQueryService.isExistingCrcoCode(crcoCode, appUser.getCompetentAuthority(), emissionTradingScheme, accountId);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Retrieves the current user associated aviation accounts")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AviationAccountSearchResults.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<AviationAccountSearchResults> getCurrentUserAviationAccounts(
            @Parameter(hidden = true)AppUser appUser,
            @RequestParam(value = "term", required = false) @Size(min = 3, max = 256) @Parameter(description = "The term to search") String term,
            @RequestParam(value = "page") @NotNull @Parameter(description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam(value = "size") @NotNull @Parameter(description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")  Long pageSize
    ) {
        return new ResponseEntity<>(
                aviationAccountQueryService.getAviationAccountsByUserAndSearchCriteria(appUser,
                        AccountSearchCriteria.builder()
                                .term(term)
                                .paging(PagingRequest.builder().pageNumber(page).pageSize(pageSize).build())
                                .build()),
                HttpStatus.OK);
    }
}
