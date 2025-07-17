package uk.gov.pmrv.api.web.controller.authorization;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityDeletionService;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.AccountOperatorAuthorityUpdateWrapperDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.AccountOperatorsUsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.service.AccountOperatorUserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.authorization.service.AccountOperatorUserAuthorityUpdateOrchestrator;

@Validated
@RestController
@RequestMapping(path = "/v1.0/operator-authorities")
@Tag(name = "Operator Authorities")
@RequiredArgsConstructor
public class OperatorAuthorityController {

    private final AccountOperatorUserAuthorityQueryOrchestrator accountOperatorUserAuthorityQueryOrchestrator;
    private final AccountOperatorUserAuthorityUpdateOrchestrator accountOperatorUserAuthorityUpdateOrchestrator;
    private final OperatorAuthorityDeletionService operatorAuthorityDeletionService;

    @GetMapping(path = "/account/{accountId}")
    @Operation(summary = "Retrieves the authorities of type OPERATOR for the given account id along with the account contact types")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AccountOperatorsUsersAuthoritiesInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AccountOperatorsUsersAuthoritiesInfoDTO> getAccountOperatorAuthorities(
            @Parameter(hidden = true) AppUser currentUser,
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId) {
        return new ResponseEntity<>(
                accountOperatorUserAuthorityQueryOrchestrator.getAccountOperatorsUsersAuthoritiesInfo(currentUser, accountId),
                HttpStatus.OK);
    }

    @PostMapping(path = "/account/{accountId}")
    @Operation(summary = "Updates authorities for users of type OPERATOR for the given account id")

    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_ACCOUNT_OPERATOR_AUTHORITY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateAccountOperatorAuthorities(
            @PathVariable("accountId") @Parameter(description = "The account id")
                    Long accountId,
            @RequestBody @Valid @Parameter(description = "The account operator authorities to update", required = true)
                    AccountOperatorAuthorityUpdateWrapperDTO accountOperatorAuthorityUpdateWrapper) {
        accountOperatorUserAuthorityUpdateOrchestrator.updateAccountOperatorAuthorities(
                accountOperatorAuthorityUpdateWrapper.getAccountOperatorAuthorityUpdateList(),
                accountOperatorAuthorityUpdateWrapper.getContactTypes(),
                accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/account/{accountId}/{userId}")
    @Operation(summary = "Deletes authority from the account")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.DELETE_ACCOUNT_OPERATOR_AUTHORITY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> deleteAccountOperatorAuthority(
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @PathVariable("userId") @Parameter(description = "The user id") String userId) {
        operatorAuthorityDeletionService.deleteAccountOperatorAuthority(userId, accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/account/{accountId}")
    @Operation(summary = "Deletes logged in authority from the account")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.DELETE_ACCOUNT_OPERATOR_AUTHORITY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> deleteCurrentUserAccountOperatorAuthority(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId) {
        operatorAuthorityDeletionService.deleteAccountOperatorAuthority(appUser.getUserId(), accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
