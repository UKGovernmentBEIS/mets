package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.datagaps.EmpDataGaps;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmpDataGapsSectionValidatorTest {

    @InjectMocks
    private EmpDataGapsSectionValidator validator;

    @Test
    void validate_when_fuel_use_valid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .dataGaps(EmpDataGaps.builder()
                                .dataGaps("data gaps")
                                .secondaryDataSources("secondary data sources")
                                .substituteData("substitute data")
                                .otherDataGapsTypes("other data gaps types")
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_when_small_emitters_valid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_when_fuel_use_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).hasSize(1);
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_DATA_GAPS.getMessage());
    }

    @Test
    void validate_when_support_facility_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                                .build())
                        .dataGaps(EmpDataGaps.builder()
                                .dataGaps("data gaps")
                                .secondaryDataSources("secondary data sources")
                                .substituteData("substitute data")
                                .otherDataGapsTypes("other data gaps types")
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).hasSize(1);
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_DATA_GAPS.getMessage());
    }
}
