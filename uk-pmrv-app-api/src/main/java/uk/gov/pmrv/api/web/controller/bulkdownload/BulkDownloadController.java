package uk.gov.pmrv.api.web.controller.bulkdownload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.bulkdownload.core.domain.dto.BulkDownloadResponse;
import uk.gov.pmrv.api.bulkdownload.core.service.BulkDownloadGenerateFileService;
import uk.gov.pmrv.api.bulkdownload.core.service.BulkDownloadDelegator;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;


import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/bulk-download")
@RequiredArgsConstructor
@Tag(name = "BulkDownload")
@Validated
public class BulkDownloadController {

    private final BulkDownloadDelegator bulkDownloadDelegator;
    private final BulkDownloadGenerateFileService bulkDownloadGenerateFileService;

    @GetMapping("/access")
    @Operation(summary = "Check if the regulator has access bulk download.")
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<Boolean> hasAccessBulkDownload(
            @Parameter(hidden = true) AppUser appUser) {

        boolean hasAccess = bulkDownloadDelegator.canBulkDownload(appUser);
        return ResponseEntity.ok(hasAccess);
    }

    @GetMapping(path = "/workflows")
    @Operation(summary = "Retrieves the list of available workflow types for bulk download.")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = String.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<List<String>> getAvailableWorkflows(@Parameter(hidden = true) AppUser appUser) {
        return new ResponseEntity<>(bulkDownloadDelegator.getAvailableWorkflows(appUser), HttpStatus.OK);
    }

    @GetMapping(path = "/workflows/{workflow}/periods")
    @Operation(summary = "Retrieves the list of periods available for bulk download for a specific workflow type.")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = String.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<List<String>> getAvailablePeriods(@Parameter(hidden = true) AppUser appUser,
                                                            @PathVariable("workflow") @Parameter(name = "workflow", description = "The workflow name (e.g. ALR, WASTE_QDR)") @NotNull String workflow) {
        return new ResponseEntity<>(bulkDownloadDelegator.getAvailablePeriods(workflow, appUser), HttpStatus.OK);
    }

    @GetMapping(path = "/workflows/{workflow}/periods/{period}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Downloads bulk export for the selected workflow and period")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @Authorized
    public ResponseEntity<FileToken> generateBulkDownloadExportToken(@Parameter(hidden = true) AppUser appUser,
                                                        @PathVariable("workflow") @Parameter(name = "workflow", description = "The workflow name (e.g. ALR, WASTE_QDR)") @NotNull String workflow,
                                                        @PathVariable("period")
            @Parameter(name = "period", description = "The period (e.g. 2025, 2025 Q1)") @NotNull String period){
        return ResponseEntity
                .ok()
                .body(bulkDownloadGenerateFileService.generateBulkDownloadAttachmentToken(workflow, period, appUser));
    }

    @GetMapping(path = "/file/{token}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Get the file attachment resource for the provided file attachment token")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(type = "string", format = "binary")))
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<StreamingResponseBody> bulkDownloadExport(
            @PathVariable("token") @Parameter(description = "The file attachment token", required = true) @NotEmpty String token) {
        BulkDownloadResponse response = bulkDownloadGenerateFileService.extractBulkDownloadResponseFromToken(token);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                .body(response.getBody());
    }
}
