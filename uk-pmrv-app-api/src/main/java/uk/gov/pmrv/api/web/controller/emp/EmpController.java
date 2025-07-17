package uk.gov.pmrv.api.web.controller.emp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpAttachmentService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpDocumentService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.UUID;

@Validated
@RestController
@RequestMapping(path = "/v1.0/emps")
@RequiredArgsConstructor
@Tag(name = "Emps")
public class EmpController {
    private final EmpAttachmentService empAttachmentService;
    private final EmpDocumentService empDocumentService;

    @GetMapping(path = "/{id}/attachments")
    @Operation(summary = "Generate the token to get the file that belongs to the provided emp id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#empId")
    public ResponseEntity<FileToken> generateGetEmpAttachmentToken(
            @PathVariable("id") @Parameter(name = "id", description = "The emp id") @NotNull String empId,
            @RequestParam("uuid") @Parameter(name = "uuid", description = "The attachment uuid") @NotNull UUID attachmentUuid) {
        FileToken getFileAttachmentToken =
                empAttachmentService.generateGetFileAttachmentToken(empId, attachmentUuid);
        return new ResponseEntity<>(getFileAttachmentToken, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/document")
    @Operation(summary = "Generate the token to get the document that belongs to the provided emp id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#empId")
    public ResponseEntity<FileToken> generateGetEmpDocumentToken(
            @PathVariable("id") @Parameter(name = "id", description = "The emp id") @NotNull String empId,
            @RequestParam("uuid") @Parameter(name = "uuid", description = "The document uuid") @NotNull UUID documentUuid) {

        final FileToken getFileDocumentToken = empDocumentService.generateGetFileDocumentToken(empId, documentUuid);
        return new ResponseEntity<>(getFileDocumentToken, HttpStatus.OK);
    }
}
