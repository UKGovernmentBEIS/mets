package uk.gov.pmrv.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationRequestPaymentDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler.PermitVariationRequestPaymentActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationRequestService;

@Validated
@RestController
@RequestMapping(path = "/v1.0/mets/requests")
@Tag(name = "Requests")
@RequiredArgsConstructor
public class PermitVariationRequestController {

    private final PermitVariationRequestPaymentActionHandler permitVariationRequestPaymentActionHandler;
    private final PermitVariationRequestService permitVariationRequestService;

    @GetMapping("/access-to-request-payment/{id}")
    @Operation(summary = "Check if the user has access to request payment for permit variation")
    @Authorized(resourceId = "#requestId")
    public ResponseEntity<Boolean> hasAccessRequestPayment(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("id") @Parameter(description = "The request id") String requestId) {

        boolean hasAccess = permitVariationRequestService.canRequestPayment(requestId);
        return ResponseEntity.ok(hasAccess);
    }

    @PostMapping("/request-payment/{id}")
    @Operation(summary = "Request payment for the given permit variation")
    @Authorized(resourceId = "#requestId")
    public ResponseEntity<Void> requestPayment(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("id") @Parameter(description = "The request id") String requestId,
            @RequestBody
            @Valid
            @Parameter(description = "The permit variation payment details", required = true)
            PermitVariationRequestPaymentDetails paymentDetails) {

        permitVariationRequestPaymentActionHandler.process(
                requestId,
                appUser,
                paymentDetails
        );

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
