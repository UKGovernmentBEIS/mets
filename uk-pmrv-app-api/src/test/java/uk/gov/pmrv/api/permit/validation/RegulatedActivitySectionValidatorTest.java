package uk.gov.pmrv.api.permit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.*;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.CapacityUnit;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RegulatedActivitySectionValidatorTest  {

    @InjectMocks
    private RegulatedActivitySectionValidator regulatedActivitySectionValidator;

    @Test
    void validatePermitContainer() {
        RegulatedActivity regulatedActivity = new RegulatedActivity();
        regulatedActivity.setType(RegulatedActivityType.WASTE);
        regulatedActivity.setCapacity(new BigDecimal("100.00"));
        regulatedActivity.setCapacityUnit(CapacityUnit.TONNES_PER_DAY);

        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().build())
                .emissionSources(
                        EmissionSources.builder().build())
                .emissionPoints(EmissionPoints.builder().build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().build())
                .monitoringApproaches(MonitoringApproaches.builder().build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity)).build())
                .build();

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        final PermitValidationResult result = regulatedActivitySectionValidator.validate(regulatedActivity, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
    }

    @Test
    void invalidatePermitContainer() {
        RegulatedActivity regulatedActivity = new RegulatedActivity();
        regulatedActivity.setType(RegulatedActivityType.COMBUSTION);
        regulatedActivity.setCapacity(new BigDecimal("100.00"));
        regulatedActivity.setCapacityUnit(CapacityUnit.TONNES_PER_DAY);

        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().build())
                .emissionSources(
                        EmissionSources.builder().build())
                .emissionPoints(EmissionPoints.builder().build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().build())
                .monitoringApproaches(MonitoringApproaches.builder().build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity)).build())
                .build();

        final PermitContainer permitContainer = PermitContainer
                .builder().permitType(PermitType.WASTE).permit(permit)
                .build();

        final PermitValidationResult result = regulatedActivitySectionValidator.validate(regulatedActivity, permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
                new PermitViolation(PermitViolation.PermitViolationMessage.INVALID_WASTE_REGULATED_ACTIVITY));
    }

}
