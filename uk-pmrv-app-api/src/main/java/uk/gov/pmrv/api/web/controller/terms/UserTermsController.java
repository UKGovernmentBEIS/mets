package uk.gov.pmrv.api.web.controller.terms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.terms.userterms.UserTermsService;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.Optional;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/user-terms")
@Tag(name = "Terms and conditions")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ui.features", name = "terms", havingValue = "true")
public class UserTermsController {

    private final UserTermsService userTermsService;

    @PatchMapping
    @Operation(summary = "Updates accepted terms and conditions of the logged in user")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserTermsVersionUpdateDTO.class))})
    @ApiResponse(responseCode = "400", description = BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<UserTermsVersionUpdateDTO> editUserTerms(
            @Parameter(hidden = true) AppUser appUser,
            @RequestBody @Valid @Parameter(description = "The updateTermsDTO", required = true) UserTermsVersionUpdateDTO userTermsVersionUpdateDTO) {
        userTermsService.updateUserTerms(appUser.getUserId(), userTermsVersionUpdateDTO.getVersion());
        return new ResponseEntity<>(userTermsVersionUpdateDTO, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Retrieves accepted terms and conditions version of the logged in user")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserTermsVersionDTO.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<UserTermsVersionDTO> getUserTerms(@Parameter(hidden = true) AppUser appUser) {
        Optional<Short> userTermsVersion = userTermsService.getUserTerms(appUser.getUserId());
        return new ResponseEntity<>(UserTermsVersionDTO.builder()
                .termsVersion(userTermsVersion.orElse(null)).build(), HttpStatus.OK);
    }

}