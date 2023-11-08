package uk.gov.pmrv.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.account.domain.dto.AccountContactDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoResponse;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import java.util.List;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@Validated
@RestController
@RequestMapping(path = "/v1.0/{accountType}/ca-site-contacts")
@RequiredArgsConstructor
@Tag(name = "Ca site contacts")
public class CaSiteContactController {

    private final AccountCaSiteContactService accountCaSiteContactService;

    /**
     * Retrieves the accounts competent authority site contacts in which Regulator user has access.
     *
     * @param user {@link PmrvUser}
     * @param accountType {@link AccountType}
     * @param page Page number
     * @param pageSize Page size number
     * @return {@link AccountContactInfoResponse}
     */
    @AuthorizedRole(roleType = REGULATOR)
    @GetMapping
    @Operation(summary = "Retrieves the accounts and competent authority site contact of the accounts")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountContactInfoResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<AccountContactInfoResponse> getCaSiteContacts(
            @Parameter(hidden = true) PmrvUser user,
            @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType,
            @RequestParam("page") @Parameter(name = "page", description = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Integer page,
            @RequestParam("size") @Parameter(name = "size", description = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Integer pageSize) {

        return new ResponseEntity<>(
            accountCaSiteContactService.getAccountsAndCaSiteContacts(user, accountType, page, pageSize),
            HttpStatus.OK
        );
    }

    /**
     * Updates accounts competent authority site contact.
     *
     * @param user {@link PmrvUser}
     * @param accountType {@link AccountType}
     * @param caSiteContacts List of {@link AccountContactDTO}
     * @return Empty response
     */
    @PostMapping
    @Operation(summary = "Updates competent authority site contacts")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_CA_SITE_CONTACTS_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> updateCaSiteContacts(
            @Parameter(hidden = true) PmrvUser user,
            @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType,
            @RequestBody @Valid @NotEmpty @Parameter(description = "The accounts with updated competent authority site contacts", required = true)
                    List<AccountContactDTO> caSiteContacts) {
        accountCaSiteContactService.updateCaSiteContacts(user, accountType, caSiteContacts);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
