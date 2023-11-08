package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingWithdrawal;

@Validated
@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesWithdrawalValidator {

    public void validate(@NotNull @Valid WithholdingWithdrawal withholdingWithdrawal) {

    }
}
