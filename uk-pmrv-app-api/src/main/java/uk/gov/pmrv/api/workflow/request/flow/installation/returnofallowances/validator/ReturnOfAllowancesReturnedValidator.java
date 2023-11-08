package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturned;

@Validated
@Service
@RequiredArgsConstructor
public class ReturnOfAllowancesReturnedValidator {

    public void validate(@NotNull @Valid ReturnOfAllowancesReturned returnOfAllowancesReturned) {

    }
}
