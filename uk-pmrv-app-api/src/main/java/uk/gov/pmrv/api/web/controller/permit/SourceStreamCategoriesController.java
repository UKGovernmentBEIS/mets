package uk.gov.pmrv.api.web.controller.permit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamTypeCategory;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@Validated
@RestController
@RequestMapping(path = "/v1.0/source-stream-categories")
@Tag(name = "Source Streams")
public class SourceStreamCategoriesController {

    @GetMapping
    @Operation(summary = "Returns source stream types with categories")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = SourceStreamTypeCategory.class))))
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = OPERATOR)
    public ResponseEntity<List<SourceStreamTypeCategory>> getSourceStreamCategories() {
        return new ResponseEntity<>(
                Stream.of(SourceStreamType.values()).map(SourceStreamTypeCategory::new).collect(Collectors.toList()),
                HttpStatus.OK
        );
    }
}
