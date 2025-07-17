package uk.gov.pmrv.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentCreateResponseDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentProcessResponseDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.CardPaymentService;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_CREATE_CARD_PAYMENT_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_PROCESS_EXISTING_CARD_PAYMENT_BAD_REQUEST;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType.PAYMENT_PAY_BY_CARD;

@RestController
@RequestMapping(path = "/v1.0/tasks-payment")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class RequestTaskPaymentController {

    private final CardPaymentService cardPaymentService;

    @PostMapping(path = "/{taskId}/create")
    @Operation(summary = "Create card payment for the provided task")
    @ApiResponse(responseCode = "200", description = OK)
    @ApiResponse(responseCode = "400", description = REQUEST_TASK_CREATE_CARD_PAYMENT_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<CardPaymentCreateResponseDTO> createCardPayment(@Parameter(hidden = true) AppUser appUser,
                                                                          @PathVariable("taskId") @Parameter(description = "The task id") Long taskId) {
        return ResponseEntity.ok(cardPaymentService.createCardPayment(taskId, PAYMENT_PAY_BY_CARD, appUser));
    }

    @PostMapping(path = "/{taskId}/process")
    @Operation(summary = "Process existing card payment that corresponds to the provided task")
    @ApiResponse(responseCode = "200", description = OK)
    @ApiResponse(responseCode = "400", description = REQUEST_TASK_PROCESS_EXISTING_CARD_PAYMENT_BAD_REQUEST, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<CardPaymentProcessResponseDTO> processExistingCardPayment(@Parameter(hidden = true) AppUser appUser,
                                                                                    @PathVariable("taskId") @Parameter(description = "The task id") Long taskId) {
        return ResponseEntity.ok(cardPaymentService.processExistingCardPayment(taskId, PAYMENT_PAY_BY_CARD, appUser));
    }
}
