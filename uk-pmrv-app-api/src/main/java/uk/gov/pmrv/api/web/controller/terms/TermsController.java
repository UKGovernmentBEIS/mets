package uk.gov.pmrv.api.web.controller.terms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.terms.Terms;
import uk.gov.netz.api.terms.TermsDTO;
import uk.gov.netz.api.terms.TermsMapper;
import uk.gov.netz.api.terms.TermsService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

/**
 * Controller for terms and conditions.
 */
@RestController
@RequestMapping(path = "/v1.0/terms")
@Tag(name = "Terms and conditions")
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ui.features", name = "terms", havingValue = "true")
public class TermsController {

    private final TermsService termsService;
    private final TermsMapper termsMapper;

    @GetMapping
    @Operation(summary = "Retrieves the latest version of terms and conditions")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TermsDTO.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<TermsDTO> getLatestTerms() {

        Terms latestTerms = termsService.getLatestTerms();

        if (latestTerms == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        TermsDTO termsDTO = termsMapper.transformToTermsDTO(latestTerms);

        return new ResponseEntity<>(termsDTO, HttpStatus.OK);
    }
    
}
