package uk.gov.pmrv.api.web.controller.mireport;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResult;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedService;
import uk.gov.netz.api.mireport.userdefined.custom.CustomMiReportQuery;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

@RestController
@RequestMapping(path = "/v1.0/mireports/user-defined")
@RequiredArgsConstructor
@Tag(name = "Mi Reports User defined")
@Validated
public class MiReportUserDefinedController {

    private final MiReportUserDefinedService miReportUserDefinedService;
    
    @PostMapping("/generate-custom")
    @Operation(summary = "Generates custom mi report user defined")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportUserDefinedResult.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
	public ResponseEntity<MiReportUserDefinedResult> generateCustomReport(@Parameter(hidden = true) AppUser appUser,
			@RequestBody @Parameter(description = "The parameters based on which the report will be generated", required = true) @Valid CustomMiReportQuery customQuery) {
		MiReportUserDefinedResult reportResult = miReportUserDefinedService
				.generateCustomReport(appUser.getCompetentAuthority(), customQuery);
		return ResponseEntity.ok(reportResult);
	}
    
}
