package uk.gov.pmrv.api.web.controller.authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.UserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserStateDTO;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/authorities")
@Tag(name = "Authorities")
@RequiredArgsConstructor
public class AuthorityController {

    private final UserAuthorityQueryOrchestrator userAuthorityQueryOrchestrator;

    @GetMapping(path = "/current-user-state")
    @Operation(summary = "Retrieves the status of the logged in user")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserStateDTO.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<UserStateDTO> getCurrentUserState(@Parameter(hidden = true) PmrvUser pmrvUser) {
        return new ResponseEntity<>(userAuthorityQueryOrchestrator.getUserState(pmrvUser), HttpStatus.OK);

    }
}
