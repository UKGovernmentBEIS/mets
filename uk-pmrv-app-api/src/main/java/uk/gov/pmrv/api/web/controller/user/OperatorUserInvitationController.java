package uk.gov.pmrv.api.web.controller.user;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserInvitationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

@RestController
@RequestMapping(path = "/v1.0/operator-users/invite")
@Tag(name = "Operator Users invitation")
@RequiredArgsConstructor
public class OperatorUserInvitationController {

    private final OperatorUserInvitationService operatorUserInvitationService;

    @PostMapping(path = "/account/{accountId}")
    @Operation(summary = "Adds a new operator user to an account with a specified role.")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.OPERATOR_USER_ACCOUNT_REGISTRATION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> inviteOperatorUserToAccount(
            @Parameter(hidden = true) AppUser currentUser,
            @PathVariable("accountId") @Parameter(description = "The account id") Long accountId,
            @RequestBody @Valid @Parameter(description = "The operator user account registration info", required = true)
                    OperatorUserInvitationDTO operatorUserInvitationDTO) {
        operatorUserInvitationService.inviteUserToAccount(accountId, operatorUserInvitationDTO, currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
