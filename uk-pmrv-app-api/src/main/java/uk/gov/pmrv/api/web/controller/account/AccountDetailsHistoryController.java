package uk.gov.pmrv.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsHistoryListResponse;
import uk.gov.pmrv.api.account.service.AccountDetailsHistoryService;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/account-details-history")
@Tag(name = "Account Details History", description = "Endpoints for account details history")
@RequiredArgsConstructor
@Validated
public class AccountDetailsHistoryController {

    private final AccountDetailsHistoryService accountDetailsHistoryService;

    @GetMapping
    @Operation(summary = "Get the account details history for an account")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountDetailsHistoryListResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<AccountDetailsHistoryListResponse> getAccountDetailsHistory(
            @RequestParam(value = "accountId") @Parameter(description = "The account Id", required = true) Long accountId,
            @RequestParam("page") @Parameter(description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") @NotNull(message = "{parameter.page.typeMismatch}") Integer page,
            @RequestParam("size") @Parameter(description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}") @NotNull(message = "{parameter.pageSize.typeMismatch}") Integer pageSize) {
        return new ResponseEntity<>(accountDetailsHistoryService.getAccountDetailsHistory(accountId, page, pageSize), HttpStatus.OK);
    }

}
