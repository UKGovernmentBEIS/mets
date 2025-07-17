package uk.gov.pmrv.api.allowance.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;

import java.util.List;

@Component
@Validated
public class AllowanceActivityLevelValidator {

    public void validate(@NotEmpty List<@NotNull @Valid ActivityLevel> activityLevels) {
        // Validate
    }
}
