package uk.gov.pmrv.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentAttachmentService;


@Validated
@RestController
@RequestMapping(path = "/v1.0/accounts/{accountId}/file-attachments")
@RequiredArgsConstructor
@Tag(name = "AccountFileAttachment")
public class AccountFileAttachmentController {
    private final AccountFileAttachmentAttachmentService accountFileAttachmentAttachmentService;


    @GetMapping
    @Operation(summary = "Generate the token to get the file that belongs to the provided account id",
            parameters = {@Parameter(name = "accountId", in = ParameterIn.PATH, description = "The account id", required = true)})
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<FileToken> generateGetFileAccountFileAttachmentToken(
            @PathVariable("accountId") @Parameter(name = "accountId", description = "The account id") @NotNull Long accountId,
            @RequestParam("uuid") @Parameter(name = "uuid", description = "The attachment uuid") @NotNull String attachmentUuid) {
        FileToken getFileAttachmentToken =
                accountFileAttachmentAttachmentService.generateGetFileAttachmentToken(accountId, attachmentUuid);
        return new ResponseEntity<>(getFileAttachmentToken, HttpStatus.OK);
    }
}
