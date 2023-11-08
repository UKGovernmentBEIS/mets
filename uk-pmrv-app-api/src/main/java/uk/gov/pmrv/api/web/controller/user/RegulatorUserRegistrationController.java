package uk.gov.pmrv.api.web.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAcceptInvitationService;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInvitationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.ENABLE_REGULATOR_USER_FROM_INVITATION_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;

@RestController
@RequestMapping(path = "/v1.0/regulator-users/registration")
@Tag(name = "Regulator users registration")
@SecurityRequirements
@RequiredArgsConstructor
public class RegulatorUserRegistrationController {

    private final RegulatorUserInvitationService regulatorUserInvitationService;
    private final RegulatorUserAcceptInvitationService regulatorUserAcceptInvitationService;

    @PostMapping(path = "/accept-invitation")
    @Operation(summary = "Accept invitation for regulator user")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvitedUserInfoDTO.class))})
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.ACCEPT_REGULATOR_USER_INVITATION_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<InvitedUserInfoDTO> acceptRegulatorInvitation(
            @RequestBody @Valid @Parameter(description = "The invitation token", required = true) TokenDTO invitationTokenDTO) {
        return new ResponseEntity<>(regulatorUserInvitationService.acceptInvitation(invitationTokenDTO.getToken()), HttpStatus.OK);
    }

    @PutMapping(path = "/enable-from-invitation")
    @Operation(summary = "Enable a new regulator user from invitation")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = ENABLE_REGULATOR_USER_FROM_INVITATION_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<Void> enableRegulatorInvitedUser(
            @RequestBody @Valid @Parameter(description = "The regulator user credentials", required = true)
                    InvitedUserEnableDTO invitedUserEnableDTO) {
        regulatorUserAcceptInvitationService.acceptAndEnableRegulatorInvitedUser(invitedUserEnableDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
