package uk.gov.pmrv.api.web.controller.workflow;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskReleaseService;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_ASSIGNMENT_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/tasks-assignment/release")
@Tag(name = "Tasks Release")
@RequiredArgsConstructor
public class RequestTaskReleaseController {

    private final RequestTaskReleaseService requestTaskReleaseService;

    /**
     * Releases a task.
     * @param taskId the task id
     */
    @DeleteMapping(path = "/{taskId}")
    @Operation(summary = "Releases a task")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = REQUEST_TASK_ASSIGNMENT_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<Void> releaseTask(
            @Parameter(description = "The task id to be released") @PathVariable("taskId") Long taskId) {
        requestTaskReleaseService.releaseTaskById(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
