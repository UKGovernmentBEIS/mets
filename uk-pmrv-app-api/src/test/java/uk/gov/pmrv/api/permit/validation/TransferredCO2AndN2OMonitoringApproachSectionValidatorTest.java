package uk.gov.pmrv.api.permit.validation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o.TransferredCO2AndN2OMonitoringApproach;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TransferredCO2AndN2OMonitoringApproachSectionValidatorTest {

    @InjectMocks
    private TransferredCO2AndN2OMonitoringApproachSectionValidator validator;

    private TransferredCO2AndN2OMonitoringApproach transferredCO2AndN2OMonitoringApproach =
        TransferredCO2AndN2OMonitoringApproach.builder().build();

    private static String deviceId1, deviceId2;
    private static MeasurementDeviceOrMethod device1, device2;

    @BeforeAll
    static void setup() {
        deviceId1 = UUID.randomUUID().toString();
        deviceId2 = UUID.randomUUID().toString();

        device1 = MeasurementDeviceOrMethod.builder().id(deviceId1).reference("deviceId1").build();
        device2 = MeasurementDeviceOrMethod.builder().id(deviceId2).reference("deviceId2").build();
    }

    @Test
    void validatePermitContainer_whenNoTransferredCO2ApproachExistsAndNoTransferOption_thenAllow() {
        final Permit permit = Permit.builder()
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                List.of(device1, device2)).build())
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(
                    Map.of(MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder().build()))
                .build())
            .build();

        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
    }

    @Test
    void validatePermitContainer_whenNoRequiredMonitoringApproachesAreSubmitted_thenDisallow() {
        final Permit permit = buildPermit(transferredCO2AndN2OMonitoringApproach, null, null, null);
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final PermitValidationResult result = validator.validate(permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(PermitViolation.PermitViolationMessage.NO_TRANSFERRED_EMISSIONS_SELECTED));
    }

    @Test
    void validatePermitContainer_whenNoRequiredMonitoringApproachesHaveTransfer_thenDisallow() {
        MeasurementOfCO2MonitoringApproach measMonitoringApproach = MeasurementOfCO2MonitoringApproach.builder()
            .hasTransfer(false)
            .build();
        MeasurementOfN2OMonitoringApproach n2OMonitoringApproach = MeasurementOfN2OMonitoringApproach.builder().hasTransfer(false).build();
        CalculationOfCO2MonitoringApproach calculationMonitoringApproach =
            CalculationOfCO2MonitoringApproach.builder().hasTransfer(false).build();
        final Permit permit = buildPermit(transferredCO2AndN2OMonitoringApproach, measMonitoringApproach,
            n2OMonitoringApproach, calculationMonitoringApproach);
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final PermitValidationResult result = validator.validate(permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(PermitViolation.PermitViolationMessage.NO_TRANSFERRED_EMISSIONS_SELECTED));
    }

    @Test
    void validatePermitContainer_whenAnyRequiredMonitoringApproachHasTransfer_thenAllow() {
        MeasurementOfCO2MonitoringApproach measMonitoringApproach = MeasurementOfCO2MonitoringApproach.builder()
            .hasTransfer(true)
            .build();
        MeasurementOfN2OMonitoringApproach n2OMonitoringApproach = MeasurementOfN2OMonitoringApproach.builder().hasTransfer(false).build();
        CalculationOfCO2MonitoringApproach calculationMonitoringApproach =
            CalculationOfCO2MonitoringApproach.builder().hasTransfer(false).build();
        final Permit permit = buildPermit(transferredCO2AndN2OMonitoringApproach, measMonitoringApproach,
            n2OMonitoringApproach, calculationMonitoringApproach);
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final PermitValidationResult result = validator.validate(permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertTrue(result.isValid());
        assertTrue(permitViolations.isEmpty());
    }

    @Test
    void validatePermitContainer_whenAnyRequiredMonitoringApproachHasTransfer_andSectionIsNull_thenDisallow() {
        MeasurementOfCO2MonitoringApproach measMonitoringApproach = MeasurementOfCO2MonitoringApproach.builder()
            .hasTransfer(true)
            .build();
        MeasurementOfN2OMonitoringApproach n2OMonitoringApproach = MeasurementOfN2OMonitoringApproach.builder().hasTransfer(false).build();
        CalculationOfCO2MonitoringApproach calculationMonitoringApproach =
            CalculationOfCO2MonitoringApproach.builder().hasTransfer(false).build();
        final Permit permit = buildPermit(null, measMonitoringApproach, n2OMonitoringApproach,
            calculationMonitoringApproach);
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final PermitValidationResult result = validator.validate(permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(PermitViolation.PermitViolationMessage.TRANSFERRED_CO2_AND_N2O_MONITORING_APPROACH_SHOULD_EXIST));
    }


    private Permit buildPermit(
        TransferredCO2AndN2OMonitoringApproach transferredCO2AndN2OMonitoringApproach,
        MeasurementOfCO2MonitoringApproach measMonitoringApproach,
        MeasurementOfN2OMonitoringApproach n2OMonitoringApproach,
        CalculationOfCO2MonitoringApproach calculationMonitoringApproach) {
        Permit permit = Permit.builder()
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                List.of(device1, device2)).build())
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(
                    new HashMap<>() {{
                        put(MonitoringApproachType.TRANSFERRED_CO2_N2O, transferredCO2AndN2OMonitoringApproach);
                    }}).build())
            .build();

        Optional.ofNullable(measMonitoringApproach)
            .ifPresent(approach ->
                permit.getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .put(MonitoringApproachType.MEASUREMENT_CO2, measMonitoringApproach));

        Optional.ofNullable(n2OMonitoringApproach)
            .ifPresent(approach ->
                permit.getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .put(MonitoringApproachType.MEASUREMENT_N2O, n2OMonitoringApproach));

        Optional.ofNullable(calculationMonitoringApproach)
            .ifPresent(approach ->
                permit.getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .put(MonitoringApproachType.CALCULATION_CO2, calculationMonitoringApproach));
        return permit;
    }
}
