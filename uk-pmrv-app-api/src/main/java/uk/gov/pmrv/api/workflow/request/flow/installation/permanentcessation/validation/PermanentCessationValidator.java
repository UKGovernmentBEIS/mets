package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessation;

@Validated
@Service
@RequiredArgsConstructor
public class PermanentCessationValidator {

    public void validate(@NotNull @Valid PermanentCessation permanentCessation) {
        //validate permanentCessation
    }
}
