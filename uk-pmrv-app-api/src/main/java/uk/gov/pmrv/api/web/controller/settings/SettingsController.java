package uk.gov.pmrv.api.web.controller.settings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.domain.SettingsSection;
import uk.gov.pmrv.api.settings.domain.dto.FeeRowDTO;
import uk.gov.pmrv.api.settings.domain.dto.FeeUpdateDTO;
import uk.gov.pmrv.api.settings.service.SettingsFeeService;
import uk.gov.pmrv.api.settings.service.SettingsService;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/{accountType}/settings")
@RequiredArgsConstructor
@Tag(name = "Settings")
@ConditionalOnProperty(prefix = "ui.features", name = "settings", havingValue = "true")
@Validated
public class SettingsController {

    private final SettingsService settingsService;
    private final SettingsFeeService settingsFeeService;

    @GetMapping
    @Operation(summary = "Retrieves the settings sections accessible to the current regulator user")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = SettingsSection.class))))
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<List<SettingsSection>> getAccessibleSections(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable @Parameter(description = "The account type") AccountType accountType) {

        return ResponseEntity.ok(settingsService.getAccessibleSections(appUser, accountType));
    }

    @GetMapping("/fees")
    @Operation(summary = "Retrieves the changeable fee rows on the Settings Fees page for the current regulator's CA")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = FeeRowDTO.class))))
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<List<FeeRowDTO>> getFees(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable @Parameter(description = "The account type") AccountType accountType) {

        return ResponseEntity.ok(settingsFeeService.getFees(appUser.getCompetentAuthority(), accountType));
    }

    @PutMapping("/fees/{id}/{feeType}")
    @Operation(summary = "Updates the fee amount for a given fee method and fee type")
    @ApiResponse(responseCode = "200", description = OK)
    @ApiResponse(responseCode = "400", description = "Bad request", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<Void> updateFee(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable @Parameter(description = "The account type") AccountType accountType,
            @PathVariable @Parameter(description = "The fee method id") Long id,
            @PathVariable @Parameter(description = "The fee type") FeeType feeType,
            @RequestBody @Valid FeeUpdateDTO dto) {

        settingsFeeService.updateFee(appUser.getCompetentAuthority(), accountType, id, feeType, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/fees/{id}/{feeType}/scheduled-change")
    @Operation(summary = "Cancels the scheduled fee change for a given fee method and fee type")
    @ApiResponse(responseCode = "200", description = OK)
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<Void> cancelScheduledFeeUpdate(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable @Parameter(description = "The account type") AccountType accountType,
            @PathVariable @Parameter(description = "The fee method id") Long id,
            @PathVariable @Parameter(description = "The fee type") FeeType feeType) {

        settingsFeeService.cancelScheduledFeeUpdate(appUser.getCompetentAuthority(), accountType, id, feeType);
        return ResponseEntity.ok().build();
    }
}
