package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps.EmpDataGaps;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmpDataGapsSectionValidator implements EmpUkEtsContextValidator {

    @Override
    public EmissionsMonitoringPlanValidationResult validate(EmissionsMonitoringPlanUkEtsContainer empContainer) {

        List<EmissionsMonitoringPlanViolation> empViolations = new ArrayList<>();
        final EmissionsMonitoringApproachType monitoringApproachType =
                empContainer.getEmissionsMonitoringPlan().getEmissionsMonitoringApproach().getMonitoringApproachType();
        final EmpDataGaps dataGaps = empContainer.getEmissionsMonitoringPlan().getDataGaps();

        if ((EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(monitoringApproachType) && dataGaps == null) ||
                (!EmissionsMonitoringApproachType.FUEL_USE_MONITORING.equals(monitoringApproachType) && dataGaps != null)) {
            empViolations.add(new EmissionsMonitoringPlanViolation(
                    EmpDataGaps.class.getSimpleName(),
                    EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_DATA_GAPS));
        }

        return EmissionsMonitoringPlanValidationResult.builder()
                .valid(empViolations.isEmpty())
                .empViolations(empViolations)
                .build();
    }
}
