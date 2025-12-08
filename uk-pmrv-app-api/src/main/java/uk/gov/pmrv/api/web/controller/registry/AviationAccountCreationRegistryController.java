package uk.gov.pmrv.api.web.controller.registry;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryManualPushAvailabilityService;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@Validated
@RestController
@RequestMapping(path = "/v1.0/aviation/registry")
@RequiredArgsConstructor
public class AviationAccountCreationRegistryController {

    private final AviationAccountRegistryManualPushAvailabilityService aviationAccountRegistryManualPushAvailabilityService;

    @GetMapping("/manual-push/{requestId}")
    @Operation(summary = "Check if the account connected to the provided request can be sent to registry")
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<Boolean> isManualPushToRegistryAvailable(@PathVariable("requestId") String requestId) {
        return new ResponseEntity<>(aviationAccountRegistryManualPushAvailabilityService.isManualPushAvailable(requestId),
                HttpStatus.OK);
    }
}
