package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowances;

@Validated
@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesValidator {

    public void validate(@NotNull @Valid WithholdingOfAllowances withholdingOfAllowances) {

    }
}
