package uk.gov.pmrv.api.web.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.feedback.UserFeedbackDto;
import uk.gov.netz.api.feedback.UserFeedbackService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.user.application.UserServiceDelegator;
import uk.gov.pmrv.api.user.core.domain.dto.RegisterUserLoginDomainDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;
import uk.gov.pmrv.api.user.core.service.UserSignatureService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorCurrentUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.UUID;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/users")
@Tag(name = "Users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceDelegator userServiceDelegator;
    private final UserSignatureService userSignatureService;
    private final UserFeedbackService userFeedbackService;
    private final UserLoginDomainService userLoginDomainService;

    @GetMapping(path = "/current")
    @Operation(summary = "Retrieves info of the logged in user")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(oneOf = {UserDTO.class, OperatorUserDTO.class, RegulatorCurrentUserDTO.class, VerifierUserDTO.class}))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<UserDTO> getCurrentUser(@Parameter(hidden = true) AppUser appUser) {
        return new ResponseEntity<>(userServiceDelegator.getCurrentUserDTO(appUser), HttpStatus.OK);
    }

    @GetMapping(path = "/signature")
    @Operation(summary = "Generate the token to get the signature of the current user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<FileToken> generateGetCurrentUserSignatureToken(
            @Parameter(hidden = true) AppUser appUser,
            @RequestParam("signatureUuid") @Parameter(name = "signatureUuid", description = "The signature uuid") @NotNull UUID signatureUuid) {
        FileToken getFileToken = userSignatureService.generateSignatureFileToken(appUser.getUserId(), signatureUuid);
        return new ResponseEntity<>(getFileToken, HttpStatus.OK);
    }

    @PostMapping(path = "/feedback")
    @Operation(summary = "Provides the feedback about the service for the logged in user")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> provideUserFeedback(
            @RequestBody @Valid @Parameter(description = "The user feedback", required = true) UserFeedbackDto userFeedbackDto) {
        String domainUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        userFeedbackService.sendFeedback(domainUrl, userFeedbackDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/last-login")
    @Operation(summary = "Updates the last login domain of the logged in user")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> registerUserLastLoginDomain(
            @Parameter(hidden = true) AppUser appUser,
            @RequestBody @Valid @Parameter(description = "The last login domain", required = true) RegisterUserLoginDomainDTO userLoginDomainDTO) {
        userLoginDomainService.registerUserLastLoginDomain(appUser.getUserId(), userLoginDomainDTO.getLoginDomain());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
