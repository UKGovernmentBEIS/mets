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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.account.domain.dto.AccountContactDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoResponse;
import uk.gov.pmrv.api.account.service.AccountVbSiteContactService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.List;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@Validated
@RestController
@RequestMapping(path = "/v1.0/{accountType}/vb-site-contacts")
@RequiredArgsConstructor
@Tag(name = "VB site contacts")
public class VbSiteContactController {

    private final AccountVbSiteContactService accountVbSiteContactService;

    /**
     * Retrieves the accounts verification body site contacts in which Verifier user has access.
     *
     * @param user {@link AppUser}
     * @param accountType {@link AccountType}
     * @param page Page number
     * @param pageSize Page size number
     * @return {@link AccountContactVbInfoResponse}
     */
    @AuthorizedRole(roleType = VERIFIER)
    @GetMapping
    @Operation(summary = "Retrieves the accounts and verification body site contact of the accounts")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountContactVbInfoResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<AccountContactVbInfoResponse> getVbSiteContacts(
            @Parameter(hidden = true) AppUser user,
            @PathVariable("accountType") @Parameter(name = "accountType", description = "The account type") AccountType accountType,
            @RequestParam("page") @Parameter(name = "page", description = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Integer page,
            @RequestParam("size") @Parameter(name = "size", description = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Integer pageSize) {

        return new ResponseEntity<>(accountVbSiteContactService.getAccountsAndVbSiteContacts(user, accountType, page, pageSize), HttpStatus.OK);
    }

    /**
     * Updates accounts verification body site contact.
     *
     * @param user {@link AppUser}
     * @param accountType {@link AccountType}
     * @param vbSiteContacts List of {@link AccountContactDTO}
     * @return Empty response
     */
    @PostMapping
    @Operation(summary = "Updates verification body site contacts")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_VB_SITE_CONTACTS_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> updateVbSiteContacts(
            @Parameter(hidden = true) AppUser user,
            @PathVariable("accountType") @Parameter(description = "The account type") AccountType accountType,
            @RequestBody @Valid @NotEmpty @Parameter(description = "The accounts with updated verification body site contacts", required = true)
                    List<AccountContactDTO> vbSiteContacts) {
        accountVbSiteContactService.updateVbSiteContacts(user, accountType, vbSiteContacts);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
