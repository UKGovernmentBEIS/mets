package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetailsType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2Direction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.MeasurementInstrumentOwnerType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.inherent.InherentCO2Emissions;

@ExtendWith(MockitoExtension.class)
public class AerInherentCO2EmissionsInitServiceTest {

    @InjectMocks
    private AerInherentCO2EmissionsInitService service;

    @Test
    void initialize() {
        InherentReceivingTransferringInstallation inherentReceivingTransferringInstallation = InherentReceivingTransferringInstallation.builder()
            .totalEmissions(BigDecimal.TEN)
            .inherentCO2Direction(InherentCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION)
            .measurementInstrumentOwnerTypes(Set.of(MeasurementInstrumentOwnerType.INSTRUMENTS_BELONGING_TO_THE_OTHER_INSTALLATION))
            .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
            .inherentReceivingTransferringInstallationDetailsType(InherentReceivingTransferringInstallationEmitter.builder()
                .installationEmitter(InstallationEmitter.builder()
                    .emitterId("emitter")
                    .email("test@test.cm")
                    .build())
                .build())
            .build();

        InherentReceivingTransferringInstallation expected = InherentReceivingTransferringInstallation.builder()
            .totalEmissions(null)
            .inherentCO2Direction(InherentCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION)
            .measurementInstrumentOwnerTypes(Set.of(MeasurementInstrumentOwnerType.INSTRUMENTS_BELONGING_TO_THE_OTHER_INSTALLATION))
            .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
            .inherentReceivingTransferringInstallationDetailsType(InherentReceivingTransferringInstallationEmitter.builder()
                .installationEmitter(InstallationEmitter.builder()
                    .emitterId("emitter")
                    .email("test@test.cm")
                    .build())
                .build())
            .build();

        Permit permit = Permit.builder()
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(
                    Map.of(
                        MonitoringApproachType.INHERENT_CO2,
                        InherentCO2MonitoringApproach.builder()
                            .type(MonitoringApproachType.INHERENT_CO2)
                            .inherentReceivingTransferringInstallations(List.of(inherentReceivingTransferringInstallation))
                            .build()
                    )
                )
                .build())
            .build();

        AerMonitoringApproachEmissions aerMonitoringApproachEmissions = service.initialize(permit);
        assertNotNull(aerMonitoringApproachEmissions);
        assertEquals(MonitoringApproachType.INHERENT_CO2, aerMonitoringApproachEmissions.getType());
        assertThat(aerMonitoringApproachEmissions).isInstanceOf(InherentCO2Emissions.class);

        InherentCO2Emissions inherentCO2Emissions = (InherentCO2Emissions) aerMonitoringApproachEmissions;

        assertEquals(1, inherentCO2Emissions.getInherentReceivingTransferringInstallations().size());
        assertThat(inherentCO2Emissions.getInherentReceivingTransferringInstallations().get(0).getInherentReceivingTransferringInstallation()
            .getTotalEmissions()).isNull();
        assertEquals(expected.getInherentCO2Direction(),
            inherentCO2Emissions.getInherentReceivingTransferringInstallations().get(0).getInherentReceivingTransferringInstallation()
                .getInherentCO2Direction());
        assertEquals(expected.getMeasurementInstrumentOwnerTypes(),
            inherentCO2Emissions.getInherentReceivingTransferringInstallations().get(0).getInherentReceivingTransferringInstallation()
                .getMeasurementInstrumentOwnerTypes());
        assertEquals(expected.getInstallationDetailsType(),
            inherentCO2Emissions.getInherentReceivingTransferringInstallations().get(0).getInherentReceivingTransferringInstallation()
                .getInstallationDetailsType());
        assertEquals(expected.getInherentReceivingTransferringInstallationDetailsType(),
            inherentCO2Emissions.getInherentReceivingTransferringInstallations().get(0).getInherentReceivingTransferringInstallation()
                .getInherentReceivingTransferringInstallationDetailsType());
    }

    @Test
    void getMonitoringApproachType() {
        assertEquals(MonitoringApproachType.INHERENT_CO2, service.getMonitoringApproachType());
    }
}
