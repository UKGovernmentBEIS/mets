package uk.gov.pmrv.api.workflow.request.flow.installation.common.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationContainer;

@Validated
@Component
public class PermitCessationValidator {

    public void validate(@NotNull @Valid @SuppressWarnings("unused") PermitCessationContainer cessationContainer) {
        // default validation
    }
}
