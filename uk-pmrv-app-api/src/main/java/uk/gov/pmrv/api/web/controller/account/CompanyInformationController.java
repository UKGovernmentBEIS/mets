package uk.gov.pmrv.api.web.controller.account;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.companieshouse.CompanyInformationService;
import uk.gov.pmrv.api.account.companieshouse.CompanyProfileDTO;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.GET_COMPANY_PROFILE_INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.GET_COMPANY_PROFILE_SERVICE_UNAVAILABLE;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/company-information")
@Tag(name = "Company Information")
@RequiredArgsConstructor
@Validated
public class CompanyInformationController {

    private final CompanyInformationService companyInformationService;

    @GetMapping("/{registrationNumber}")
    @Operation(summary = "Retrieves information about the company that corresponds to the provided registration number")
    @ApiResponse(responseCode = "200", description = OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CompanyProfileDTO.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = GET_COMPANY_PROFILE_INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "503", description = GET_COMPANY_PROFILE_SERVICE_UNAVAILABLE, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<CompanyProfileDTO> getCompanyProfileByRegistrationNumber(
            @Parameter(description = "The company registration number") @PathVariable("registrationNumber") String registrationNumber) {
        return new ResponseEntity<>(companyInformationService.getCompanyProfile(registrationNumber, CompanyProfileDTO.class), HttpStatus.OK);
    }

}
