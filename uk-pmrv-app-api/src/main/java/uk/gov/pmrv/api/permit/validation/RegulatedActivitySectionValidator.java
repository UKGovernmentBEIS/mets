package uk.gov.pmrv.api.permit.validation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.*;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivity;
import uk.gov.pmrv.api.reporting.validation.AerContextValidator;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class RegulatedActivitySectionValidator implements PermitSectionContextValidator<RegulatedActivity>, AerContextValidator {

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
                permitViolations.add(new PermitViolation(PermitViolation.PermitViolationMessage.INVALID_WASTE_REGULATED_ACTIVITY));
            }
        }

        return PermitValidationResult.builder()
                .valid(permitViolations.isEmpty())
                .permitViolations(permitViolations)
                .build();
    }

    @Override
    public AerValidationResult validate(AerContainer aerContainer) {

        List<AerViolation> aerViolations = new ArrayList<>();


        if(PermitType.WASTE.equals(aerContainer.getPermitOriginatedData().getPermitType())) {
            List<AerRegulatedActivity> regulatedActivities = aerContainer.getAer().getRegulatedActivities().getRegulatedActivities();
            boolean hasWaste = regulatedActivities.stream()
                    .anyMatch(activity -> RegulatedActivityType.WASTE.equals(activity.getType()));
            if(!hasWaste) {
                aerViolations.add(new AerViolation(AerContainer.class.getSimpleName(),
                        AerViolation.AerViolationMessage.INVALID_WASTE_REGULATED_ACTIVITY));
            }
        }

        return AerValidationResult.builder()
                .valid(aerViolations.isEmpty())
                .aerViolations(aerViolations)
                .build();
    }
}
