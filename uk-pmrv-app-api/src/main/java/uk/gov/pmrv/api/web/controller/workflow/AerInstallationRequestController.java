package uk.gov.pmrv.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.security.Authorized;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerMarkNotRequiredDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler.AerMarkNotRequiredActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerRequestService;

@Validated
@RestController
@RequestMapping(path = "/v1.0/mets/requests")
@Tag(name = "Requests")
@RequiredArgsConstructor
public class AerInstallationRequestController {

    private final AerMarkNotRequiredActionHandler aerMarkNotRequiredActionHandler;

    private final AerRequestService aerRequestService;


    @GetMapping("/access-to-mark-as-not-required/{id}")
    @Operation(summary = "Check if the user has access to mark as not required")
    @Authorized(resourceId = "#requestId")
    public ResponseEntity<Boolean> hasAccessMarkAsNotRequired(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("id") @Parameter(description = "The request id") String requestId) {

        boolean hasAccess = aerRequestService.canMarkAsNotRequired(requestId, appUser);
        return ResponseEntity.ok(hasAccess);
    }


    @PostMapping("/mark-as-not-required/{id}")
    @Operation(summary = "Mark as not required the given request and terminate Aer workflow")
    @Authorized(resourceId = "#requestId")
    public ResponseEntity<String> markAsNotRequired(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("id") @Parameter(description = "The request id") String requestId,
            @RequestBody @Valid @Parameter(description = "The mark not required payload", required = true)
            AerMarkNotRequiredDetails aerMarkNotRequiredDetails){

        aerMarkNotRequiredActionHandler.process(requestId, appUser, aerMarkNotRequiredDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
