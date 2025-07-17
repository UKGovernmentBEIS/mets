package uk.gov.pmrv.api.web.controller.workflow;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.workflow.request.application.filedocument.requestaction.RequestActionFileDocumentService;

import java.util.UUID;

@RestController
@RequestMapping(path = "/v1.0/request-action-file-documents")
@Tag(name = "Request action file documents handling")
@RequiredArgsConstructor
public class RequestActionFileDocumentController {

    private final RequestActionFileDocumentService requestActionFileDocumentService;

    @GetMapping(path = "/{id}")
    @Operation(summary = "Generate the token to get the file document with the provided uuid that belongs to the provided request action")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#requestActionId")
    public ResponseEntity<FileToken> generateRequestActionGetFileDocumentToken(
            @PathVariable("id") @Parameter(description = "The request action id") Long requestActionId,
            @RequestParam("fileDocumentUuid") @Parameter(name = "fileDocumentUuid", description = "The file document uuid") @NotNull UUID fileDocumentUuid) {
        FileToken getFileDocumentToken =
                requestActionFileDocumentService.generateGetFileDocumentToken(requestActionId, fileDocumentUuid);
        return new ResponseEntity<>(getFileDocumentToken, HttpStatus.OK);
    }
}
