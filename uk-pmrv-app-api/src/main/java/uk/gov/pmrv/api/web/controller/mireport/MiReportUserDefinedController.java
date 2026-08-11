package uk.gov.pmrv.api.web.controller.mireport;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedDTO;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResult;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedResults;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedService;
import uk.gov.netz.api.mireport.userdefined.MiReportUserDefinedUpdateDTO;
import uk.gov.netz.api.mireport.userdefined.category.MiReportUserDefinedCategoryDTO;
import uk.gov.netz.api.mireport.userdefined.category.MiReportUserDefinedCategoryService;
import uk.gov.netz.api.mireport.userdefined.custom.CustomMiReportQuery;
import uk.gov.netz.api.mireport.userdefined.favourite.MiReportUserDefinedFavouriteService;
import uk.gov.netz.api.mireport.userdefined.history.MiReportUserDefinedHistoryResults;
import uk.gov.netz.api.mireport.userdefined.history.MiReportUserDefinedHistoryService;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.userdefined.PmrvMiReportUserDefinedService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.List;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/mireports/user-defined")
@RequiredArgsConstructor
@Tag(name = "Mi Reports User defined")
@Validated
public class MiReportUserDefinedController {

    private final MiReportUserDefinedService miReportUserDefinedService;
    private final MiReportUserDefinedCategoryService miReportUserDefinedCategoryService;
    private final MiReportUserDefinedHistoryService miReportUserDefinedHistoryService;
    private final PmrvMiReportUserDefinedService pmrvMiReportUserDefinedService;
    private final MiReportUserDefinedFavouriteService miReportUserDefinedFavouriteService;

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

    @PostMapping("/preview")
    @Operation(summary = "Previews custom mi report user defined")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportUserDefinedResult.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportUserDefinedResult> previewCustomReport(@Parameter(hidden = true) AppUser appUser,
                                                                          @RequestBody @Parameter(description = "The parameters based on which the report will be generated", required = true) @Valid CustomMiReportQuery customQuery) {
        MiReportUserDefinedResult reportResult = miReportUserDefinedService
                .previewCustomReport(appUser.getCompetentAuthority(), customQuery);
        return ResponseEntity.ok(reportResult);
    }

    @PostMapping("/create/{accountType}")
    @Operation(summary = "Creates a user defined custom mi report")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> createCustomReport(@Parameter(hidden = true) AppUser appUser,
                                                   @PathVariable @Parameter(description = "The account type") AccountType accountType,
                                                   @RequestBody @Parameter(description = "The user defined query information container ", required = true) @Valid MiReportUserDefinedDTO miReportUserDefinedDTO) {
        pmrvMiReportUserDefinedService.create(appUser, accountType, miReportUserDefinedDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/categories")
    @Operation(summary = "Retrieves all enabled user defined mi report categories")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = MiReportUserDefinedCategoryDTO.class))))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<List<MiReportUserDefinedCategoryDTO>> getCategories() {
        List<MiReportUserDefinedCategoryDTO> miReportUserDefinedCategoryDTOS = miReportUserDefinedCategoryService.findAllEnabled();
        return ResponseEntity.ok(miReportUserDefinedCategoryDTOS);
    }

    @GetMapping("/reports/{accountType}")
    @Operation(summary = "Retrieves all enabled user defined mi reports")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportUserDefinedResults.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportUserDefinedResults> getReports(@Parameter(hidden = true) AppUser appUser,
       @PathVariable @Parameter(description = "The account type") AccountType accountType,
       @RequestParam(value = "page") @NotNull @Parameter(description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Integer page,
       @RequestParam(value = "size") @NotNull @Parameter(description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")  Integer pageSize,
       @RequestParam(value = "categoryId", required = false) @Parameter(description = "Optional category id to filter by") Long categoryId,
       @RequestParam(value = "term", required = false) @Size(min = 3, max = 256) @Parameter(description = "Optional report search term") String term,
       @RequestParam(value = "favourites", required = false) @Parameter(description = "Optional filter to fetch the user's favourites") boolean favourites) {
        MiReportUserDefinedResults miReportUserDefinedResults =
                pmrvMiReportUserDefinedService.findAllByCA(appUser, accountType, page, pageSize, categoryId, term, favourites);
        return ResponseEntity.ok(miReportUserDefinedResults);
    }

    @GetMapping
    @Operation(summary = "Retrieves the report that matches the provided id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportUserDefinedDTO.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportUserDefinedDTO> getReport(@Parameter(hidden = true) AppUser appUser,
                                                            @RequestParam(value = "id")
                                    @NotNull @Parameter(description = "The unique identifier of the report") Long id){
        MiReportUserDefinedDTO miReportUserDefinedDTO = miReportUserDefinedService.findById(appUser, id);
        return ResponseEntity.ok(miReportUserDefinedDTO);
    }

    @DeleteMapping
    @Operation(summary = "Deletes the report that matches the provided id")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> deleteCustomReport(@RequestParam(value = "id")
                                    @NotNull @Parameter(description = "The unique identifier of the report") Long id) {
        miReportUserDefinedService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    @Operation(summary = "Updates the report that matches the provided id")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized
    public ResponseEntity<Void> updateCustomReport(@Parameter(hidden = true) AppUser appUser,
                                             @RequestParam(value = "id") @NotNull @Parameter(description = "The unique identifier of the report") Long id,
                                             @RequestBody @Parameter(description = "The user defined query information container", required = true) @Valid MiReportUserDefinedUpdateDTO miReportUserDefinedUpdateDTO) {
        miReportUserDefinedService.update(id, appUser, miReportUserDefinedUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/history/{miReportId}")
    @Operation(summary = "Retrieves the history of a user defined mi report")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MiReportUserDefinedHistoryResults.class)))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<MiReportUserDefinedHistoryResults> getHistory(@Parameter(hidden = true) AppUser appUser,
                                                                 @PathVariable @Parameter(description = "The id of the report to fetch the history for") Long miReportId,
                                                                 @RequestParam(value = "page") @NotNull @Parameter(description = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Integer page,
                                                                 @RequestParam(value = "size") @NotNull @Parameter(description = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")  Integer pageSize) {
        MiReportUserDefinedHistoryResults miReportUserDefinedHistoryResults = miReportUserDefinedHistoryService.findByMiReportUserDefinedId(miReportId, page, pageSize);
        return ResponseEntity.ok(miReportUserDefinedHistoryResults);
    }

    @PostMapping("/favourites")
    @Operation(summary = "Adds the mi report that corresponds to the provided id to the user's favourites")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<Void> createFavourite(@Parameter(hidden = true) AppUser appUser,
                                                @RequestParam(value = "miReportId") @Parameter(description = "The id of the user defined mi report", required = true) Long miReportId) {
        miReportUserDefinedFavouriteService.addFavourite(appUser, miReportId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/favourites")
    @Operation(summary = "Removes the mi report that corresponds to the provided id from the user's favourites")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.MI_REPORT_REQUEST_TYPE_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<Void> deleteFavourite(@Parameter(hidden = true) AppUser appUser,
                                                @RequestParam(value = "miReportId") @Parameter(description = "The id of the user defined mi report", required = true) Long miReportId) {
        miReportUserDefinedFavouriteService.removeFavourite(appUser, miReportId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/manage")
    @Operation(summary = "Check if the regulator can manage (create/update/delete) custom mi reports")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = {REGULATOR})
    public ResponseEntity<Boolean> hasManageCustomReportsAccess(@Parameter(hidden = true) AppUser appUser) {
        return ResponseEntity.ok(pmrvMiReportUserDefinedService.canManageCustomReports(appUser));
    }

}
