package uk.gov.pmrv.api.web.controller.workflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesReopenAvailabilityService;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@Validated
@RestController
@RequestMapping(path = "/v1.0/mets/requests/withhold")
@Tag(name = "Withhold Flag Requests")
@RequiredArgsConstructor
public class WithholdFlagRequestController {

    private final WithholdingOfAllowancesReopenAvailabilityService availabilityService;

    @GetMapping("/reopen-available/{accountId}")
    @Operation(summary = "Check if the account can reopen a completed withhold flag workflow")
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<Boolean> isWithholdFlagReopenAvailable(@PathVariable("accountId") Long accountId) {
        return new ResponseEntity<>(availabilityService.isWithholdReopenAvailable(accountId),
                HttpStatus.OK);
    }



}
