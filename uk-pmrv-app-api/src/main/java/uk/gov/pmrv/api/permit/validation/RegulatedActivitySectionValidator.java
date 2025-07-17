package uk.gov.pmrv.api.permit.validation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.*;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.INVALID_WASTE_REGULATED_ACTIVITY;

@Component
@RequiredArgsConstructor
public class RegulatedActivitySectionValidator implements PermitSectionContextValidator<RegulatedActivity> {

    @Override
    public PermitValidationResult validate(@Valid RegulatedActivity regulatedActivity,
                                           PermitContainer permitContainer) {

        List<PermitViolation> permitViolations = new ArrayList<>();

        if (regulatedActivity == null) {
            return PermitValidationResult.validPermit();
        }

        if(PermitType.WASTE.equals(permitContainer.getPermitType())) {
            List<RegulatedActivity> regulatedActivities = permitContainer.getPermit().getRegulatedActivities().getRegulatedActivities();
            boolean hasWaste = regulatedActivities.stream()
                    .anyMatch(activity -> RegulatedActivityType.WASTE.equals(activity.getType()));
            if(!hasWaste) {
                permitViolations.add(new PermitViolation(INVALID_WASTE_REGULATED_ACTIVITY));
            }
        }

        return PermitValidationResult.builder()
                .valid(permitViolations.isEmpty())
                .permitViolations(permitViolations)
                .build();
    }

}
