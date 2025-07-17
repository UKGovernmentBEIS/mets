package uk.gov.pmrv.api.web.controller.account.installation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@Validated
@RequestMapping(path = "/v1.0/installation/accounts")
@RequiredArgsConstructor
@Tag(name = "Installation accounts")
public class InstallationAccountController {

    private final AccountQueryService accountQueryService;
    private final InstallationAccountQueryService installationAccountQueryService;

    @GetMapping
    @Operation(summary = "Retrieves the current user associated accounts")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountSearchResults.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<AccountSearchResults> getCurrentUserInstallationAccounts(
            @Parameter(hidden = true) AppUser appUser,
            @RequestParam(value = "term", required = false) @Size(min = 3, max = 256) @Parameter(name="term", description = "The term to search") String term,
            @RequestParam(value = "page") @NotNull @Parameter(name="page", description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam(value = "size") @NotNull @Parameter(name="size", description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")  Long pageSize
    ) {
        return new ResponseEntity<>(
            installationAccountQueryService.getAccountsByUserAndSearchCriteria(appUser,
                    AccountSearchCriteria.builder()
                        .term(term)
                        .paging(PagingRequest.builder().pageNumber(page).pageSize(pageSize).build())
                        .build()),
            HttpStatus.OK);
    }

    /**
     * Checks if there is an account with the given name but different account Id than the given one
     * Account Id parameter is optional. If it is omitted method checks if an account exists by name
     * else checks by name and different account Id than the given one
     * @param accountName   the account name to look for
     * @param accountId     the account Id (optional)
     */
    @GetMapping("/name")
    @Operation(summary = "Checks if account name exists")
    @ApiResponse(responseCode = "200", description = OK)
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Boolean> isExistingAccountName(
            @Parameter(name ="name", description = "The account name") @RequestParam("name") String accountName,
            @Parameter(name="accountId", description = "The account Id") @RequestParam(value = "accountId", required = false) Long accountId) {
        boolean exists = accountQueryService.isExistingActiveAccountName(accountName, accountId);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}
