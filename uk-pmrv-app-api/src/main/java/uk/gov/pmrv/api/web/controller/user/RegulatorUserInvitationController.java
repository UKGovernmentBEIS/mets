package uk.gov.pmrv.api.web.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInvitationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.util.FileDtoMapper;

import java.io.IOException;

@RestController
@RequestMapping(path = "/v1.0/regulator-users/invite")
@Tag(name = "Regulator Users")
@RequiredArgsConstructor
@Validated
public class RegulatorUserInvitationController {

    private final RegulatorUserInvitationService regulatorUserInvitationService;
    private final FileDtoMapper fileDtoMapper = Mappers.getMapper(FileDtoMapper.class);

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Invite new regulator user")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.INVITE_REGULATOR_USER_TO_CA_BAD_REQUEST ,content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> inviteRegulatorUserToCA(
            @Parameter(hidden = true) AppUser currentUser,
            @RequestPart @Valid @Parameter(description = "The regulator to invite", required = true) RegulatorInvitedUserDTO regulatorInvitedUser,
            @RequestPart(value = "signature", required = false) @Parameter(description = "The signature file") MultipartFile signature) throws IOException {
        FileDTO signatureDTO = fileDtoMapper.toFileDTO(signature);
        regulatorUserInvitationService.inviteRegulatorUser(regulatorInvitedUser, signatureDTO, currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
