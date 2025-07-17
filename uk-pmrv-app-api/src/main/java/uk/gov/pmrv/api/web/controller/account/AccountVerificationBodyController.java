package uk.gov.pmrv.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.account.domain.dto.AppointVerificationBodyDTO;
import uk.gov.pmrv.api.account.service.AccountVerificationBodyAppointService;
import uk.gov.pmrv.api.account.service.AccountVerificationBodyService;
import uk.gov.pmrv.api.account.service.AccountVerificationBodyUnappointService;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.List;

@RestController
@RequestMapping(path = "/v1.0/accounts")
@RequiredArgsConstructor
@Tag(name = "Account verification body")
public class AccountVerificationBodyController {

    private final AccountVerificationBodyService accountVerificationBodyService;
    private final AccountVerificationBodyAppointService accountVerificationBodyAppointService;
    private final AccountVerificationBodyUnappointService accountVerificationBodyUnappointService;

    @GetMapping("/{id}/verification-body")
    @Operation(summary = "Get the verification body of the account (if exists)")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VerificationBodyNameInfoDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<VerificationBodyNameInfoDTO> getVerificationBodyOfAccount(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId) {
        return new ResponseEntity<>(
                accountVerificationBodyService.getVerificationBodyNameInfoByAccount(accountId).orElse(null),
                HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/active-verification-bodies")
    @Operation(summary = "Get all active verification bodies")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = VerificationBodyNameInfoDTO.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<List<VerificationBodyNameInfoDTO>> getActiveVerificationBodies(
            @PathVariable("id") @Parameter(description = "The account id", required = true) Long accountId) {
        return new ResponseEntity<>(
                accountVerificationBodyService.getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme(accountId),
                HttpStatus.OK);
    }

    @PostMapping(path = "/{id}/appoint-verification-body")
    @Operation(summary = "Appoint verification body to account")

    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.APPOINT_VERIFICATION_BODY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> appointVerificationBodyToAccount(
            @PathVariable("id") @Parameter(description = "The account id", required = true)
                    Long accountId,
            @RequestBody @Valid @Parameter(description = "The verification body id to appoint to account", required = true)
                    AppointVerificationBodyDTO appointVerificationBodyDTO) {
        accountVerificationBodyAppointService.appointVerificationBodyToAccount(appointVerificationBodyDTO.getVerificationBodyId(), accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/{id}/unappoint-verification-body")
    @Operation(summary = "Unappoint verification body from account")

    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UNAPPOINT_VERIFICATION_BODY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> unappointVerificationBodyFromAccount(
        @PathVariable("id") @Parameter(description = "The account id to unappoint the verification body from", required = true)
        Long accountId) {
        accountVerificationBodyUnappointService.unappointAccountAppointedToVerificationBody(accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/{id}/appoint-verification-body")
    @Operation(summary = "Reappoint verification body to account")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.REAPPOINT_VERIFICATION_BODY_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> replaceVerificationBodyToAccount(
            @PathVariable("id") @Parameter(description = "The account id", required = true)
                    Long accountId,
            @RequestBody @Valid @Parameter(description = "The verification body id to appoint to account", required = true)
                    AppointVerificationBodyDTO appointVerificationBodyDTO) {
        accountVerificationBodyAppointService.replaceVerificationBodyToAccount(appointVerificationBodyDTO.getVerificationBodyId(), accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
