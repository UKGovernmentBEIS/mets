package uk.gov.pmrv.api.web.controller.uiconfiguration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.orchestrator.uiconfiguration.UIPropertiesDTO;
import uk.gov.pmrv.api.web.orchestrator.uiconfiguration.UIPropertiesResolver;

@RestController
@RequestMapping(path = "/ui-configuration")
@RequiredArgsConstructor
@Tag(name = "UI Configuration")
@SecurityRequirements
public class UIConfigurationController {

	private final UIPropertiesResolver uiPropertiesResolver;

    @GetMapping
    @Operation(summary = "Retrieves configuration for UI features")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UIPropertiesDTO.class))})
    public ResponseEntity<UIPropertiesDTO> getUIConfiguration() {
        return ResponseEntity.ok().body(uiPropertiesResolver.resolve());
    }
}
