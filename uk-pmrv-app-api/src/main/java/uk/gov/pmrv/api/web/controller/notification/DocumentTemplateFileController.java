package uk.gov.pmrv.api.web.controller.notification;

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
import uk.gov.pmrv.api.notification.template.service.DocumentTemplateFileService;
import uk.gov.pmrv.api.token.FileToken;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;

import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/v1.0/document-template-files")
@RequiredArgsConstructor
@Tag(name = "Document Template Files")
public class DocumentTemplateFileController {

    private final DocumentTemplateFileService documentTemplateFileService;

    @GetMapping("/{id}")
    @Operation(summary = "Generates the token to get the file with the provided uuid that belongs to the provided document template")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#documentTemplateId")
    public ResponseEntity<FileToken> generateGetDocumentTemplateFileToken(
            @Parameter(description = "The document template id") @PathVariable("id") Long documentTemplateId,
            @RequestParam("fileUuid") @Parameter(name = "fileUuid", description = "The file uuid") @NotNull UUID fileUuid) {
        FileToken token = documentTemplateFileService.generateGetFileDocumentTemplateToken(documentTemplateId, fileUuid);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
