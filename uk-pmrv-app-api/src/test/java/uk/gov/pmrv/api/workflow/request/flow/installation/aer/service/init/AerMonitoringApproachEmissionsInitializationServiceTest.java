package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;

@ExtendWith(MockitoExtension.class)
class AerMonitoringApproachEmissionsInitializationServiceTest {

    @InjectMocks
    private AerMonitoringApproachEmissionsInitializationService service;

    @Spy
    private ArrayList<AerMonitoringApproachTypeEmissionsInitService> aerMonitoringApproachTypeEmissionsInitServices;

    @Mock
    private AerCalculationEmissionsInitService aerCalculationEmissionsInitService;

    @Mock
    private AerMeasurementCO2EmissionsInitService aerMeasurementCO2EmissionsInitService;

    @BeforeEach
    void setUp() {
        aerMonitoringApproachTypeEmissionsInitServices.add(aerCalculationEmissionsInitService);
        aerMonitoringApproachTypeEmissionsInitServices.add(aerMeasurementCO2EmissionsInitService);

    }

    @Test
    void initialize() {
        Aer aer = Aer.builder().build();
        Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches = new EnumMap<>(MonitoringApproachType.class);
        monitoringApproaches.put(MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder().build());
        monitoringApproaches.put(MonitoringApproachType.CALCULATION_PFC, CalculationOfPFCMonitoringApproach.builder().build());

        Permit permit = Permit.builder()
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(monitoringApproaches)
                                .build())
                        .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder().type(MonitoringApproachType.CALCULATION_CO2).build();

        when(aerCalculationEmissionsInitService.getMonitoringApproachType()).thenReturn(MonitoringApproachType.CALCULATION_CO2);

        when(aerCalculationEmissionsInitService.initialize(permit)).thenReturn(calculationEmissions);

        service.initialize(aer, permit);

        assertNotNull(aer.getMonitoringApproachEmissions());
        Map<MonitoringApproachType, AerMonitoringApproachEmissions> aerMonitoringApproachEmissions =
                aer.getMonitoringApproachEmissions().getMonitoringApproachEmissions();
        assertThat(aerMonitoringApproachEmissions).hasSize(1);
        assertThat(aerMonitoringApproachEmissions).containsOnlyKeys(MonitoringApproachType.CALCULATION_CO2);
        assertEquals(calculationEmissions, aerMonitoringApproachEmissions.get(MonitoringApproachType.CALCULATION_CO2));
        assertNull(aerMonitoringApproachEmissions.get(MonitoringApproachType.CALCULATION_PFC));

    }

    @Test
    void initialize_measurement() {
        Aer aer = Aer.builder().build();
        Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches = new EnumMap<>(MonitoringApproachType.class);
        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT_CO2, MeasurementOfCO2MonitoringApproach.builder().build());
        monitoringApproaches.put(MonitoringApproachType.CALCULATION_PFC, CalculationOfPFCMonitoringApproach.builder().build());

        Permit permit = Permit.builder()
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(monitoringApproaches)
                        .build())
                .build();
        MeasurementOfCO2Emissions measurementEmissions = MeasurementOfCO2Emissions.builder().type(MonitoringApproachType.MEASUREMENT_CO2).build();

        when(aerMeasurementCO2EmissionsInitService.getMonitoringApproachType()).thenReturn(MonitoringApproachType.MEASUREMENT_CO2);

        when(aerMeasurementCO2EmissionsInitService.initialize(permit)).thenReturn(measurementEmissions);

        service.initialize(aer, permit);

        assertNotNull(aer.getMonitoringApproachEmissions());
        Map<MonitoringApproachType, AerMonitoringApproachEmissions> aerMonitoringApproachEmissions =
                aer.getMonitoringApproachEmissions().getMonitoringApproachEmissions();
        assertThat(aerMonitoringApproachEmissions).hasSize(1);
        assertThat(aerMonitoringApproachEmissions).containsOnlyKeys(MonitoringApproachType.MEASUREMENT_CO2);
        assertEquals(measurementEmissions, aerMonitoringApproachEmissions.get(MonitoringApproachType.MEASUREMENT_CO2));
        assertNull(aerMonitoringApproachEmissions.get(MonitoringApproachType.CALCULATION_PFC));

    }
}