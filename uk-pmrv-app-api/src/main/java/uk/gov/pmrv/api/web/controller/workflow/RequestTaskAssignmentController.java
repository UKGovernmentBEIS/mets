package uk.gov.pmrv.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.AssigneeUserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.RequestTaskAssignmentDTO;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentQueryService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UserRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_ASSIGNMENT_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_CANDIDATE_ASSIGNEES_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_TYPE_CANDIDATE_ASSIGNEES_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/tasks-assignment")
@Tag(name = "Tasks Assignment")
@RequiredArgsConstructor
public class RequestTaskAssignmentController {

    private final UserRequestTaskAssignmentService userRequestTaskAssignmentService;
    private final RequestTaskAssignmentQueryService requestTaskAssignmentQueryService;

    /**
     * Assigns a task to a user.
     * @param requestTaskAssignmentDTO the {@link RequestTaskAssignmentDTO}
     */
    @PostMapping(path = "/assign")
    @Operation(summary = "Assigns a task to a user")
    @ApiResponse(responseCode = "204", description = NO_CONTENT)
    @ApiResponse(responseCode = "400", description = REQUEST_TASK_ASSIGNMENT_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#requestTaskAssignmentDTO.taskId")
    public ResponseEntity<Void> assignTask(
            @RequestBody @Valid @Parameter(description = "The request task assignment body", required = true)
                    RequestTaskAssignmentDTO requestTaskAssignmentDTO) {
        userRequestTaskAssignmentService.assignTask(requestTaskAssignmentDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieves a list of users that can be assigned to the provided task id.
     * @param taskId the task id
     * @return {@link List} of {@link UserInfo}
     */
    @GetMapping(path = "/{taskId}/candidate-assignees")
    @Operation(summary = "Returns all users to whom can be assigned the provided task ")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = AssigneeUserInfoDTO.class))))
    @ApiResponse(responseCode = "400", description = REQUEST_TASK_CANDIDATE_ASSIGNEES_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<List<AssigneeUserInfoDTO>> getCandidateAssigneesByTaskId(
            @Parameter(hidden = true) PmrvUser user,
            @Parameter(description = "The task id") @PathVariable("taskId") Long taskId) {
        return new ResponseEntity<>(
                requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(taskId, user), HttpStatus.OK);
    }

    @GetMapping(path = "/{taskId}/candidate-assignees/{taskType}")
    @Operation(summary = "Returns all users to whom can be assigned the provided task type")
    @ApiResponse(responseCode = "200", description = OK, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = AssigneeUserInfoDTO.class))))
    @ApiResponse(responseCode = "400", description = REQUEST_TASK_TYPE_CANDIDATE_ASSIGNEES_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<List<AssigneeUserInfoDTO>> getCandidateAssigneesByTaskType(
            @Parameter(hidden = true) PmrvUser user,
            @Parameter(description = "The current task id that user works on. Not related to the task type for which we search candidate assignees")
            @PathVariable("taskId") Long taskId,
            @Parameter(description = "The task type for which you need to retrieve candidate assignees")
            @PathVariable("taskType") RequestTaskType taskType) {
        return new ResponseEntity<>(
                requestTaskAssignmentQueryService.getCandidateAssigneesByTaskType(taskType, user), HttpStatus.OK);
    }
}
