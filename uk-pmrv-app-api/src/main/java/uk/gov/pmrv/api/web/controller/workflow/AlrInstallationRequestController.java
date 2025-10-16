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
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AlrMarkNotRequiredDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.handler.AlrMarkNotRequiredActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AlrRequestService;

@Validated
@RestController
@RequestMapping(path = "/v1.0/mets/requests/alr")
@Tag(name = "Requests")
@RequiredArgsConstructor
public class AlrInstallationRequestController {

    private final AlrMarkNotRequiredActionHandler alrMarkNotRequiredActionHandler;

    private final AlrRequestService alrRequestService;

    @GetMapping("/access-to-mark-as-not-required/{id}")
    @Operation(summary = "Check if the user has access to mark as not required for an alr workflow")
    @Authorized(resourceId = "#requestId")
    public ResponseEntity<Boolean> hasAccessMarkAsNotRequiredAlr(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("id") @Parameter(description = "The request id") String requestId) {

        boolean hasAccess = alrRequestService.userCanMarkAlrAsNotRequired(requestId, appUser);
        return ResponseEntity.ok(hasAccess);
    }

    @PostMapping("/mark-as-not-required/{id}")
    @Operation(summary = "Mark as not required the given request and terminate Alr workflow")
    @Authorized(resourceId = "#requestId")
    public ResponseEntity<String> markAsNotRequiredAlr(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("id") @Parameter(description = "The request id") String requestId,
            @RequestBody @Valid @Parameter(description = "The mark not required payload", required = true)
            AlrMarkNotRequiredDetails alrMarkNotRequiredDetails){

        alrMarkNotRequiredActionHandler.process(requestId, appUser, alrMarkNotRequiredDetails);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
