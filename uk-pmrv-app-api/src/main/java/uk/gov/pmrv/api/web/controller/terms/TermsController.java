package uk.gov.pmrv.api.web.controller.terms;

import io.swagger.v3.oas.annotations.Operation;
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
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.terms.domain.Terms;
import uk.gov.pmrv.api.terms.domain.dto.TermsDTO;
import uk.gov.pmrv.api.terms.service.TermsService;
import uk.gov.pmrv.api.terms.transform.TermsMapper;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

/**
 * Controller for terms and conditions.
 */
@RestController
@RequestMapping(path = "/v1.0/terms")
@Tag(name = "Terms and conditions")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService termsService;

    private final TermsMapper termsMapper;

    /**
     * Retrieves the latest version of terms and conditions
     */
    @GetMapping
    @Operation(summary = "Retrieves the latest version of terms and conditions")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TermsDTO.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<TermsDTO> getLatestTerms() {

        Terms latestTerms = termsService.getLatestTerms();

        if (latestTerms == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        TermsDTO termsDTO = termsMapper.transformToTermsDTO(latestTerms);

        return new ResponseEntity<>(termsDTO, HttpStatus.OK);

    }

}
