package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentReceivingTransferringInstallation;
import uk.gov.pmrv.api.permit.mapper.PermitEntityMapper;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.inherent.AerInherentReceivingTransferringInstallation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.inherent.InherentCO2Emissions;

@Service
public class AerInherentCO2EmissionsInitService implements AerMonitoringApproachTypeEmissionsInitService {

    private static final PermitEntityMapper PERMIT_ENTITY_MAPPER = Mappers.getMapper(PermitEntityMapper.class);

    @Override
    public AerMonitoringApproachEmissions initialize(Permit permit) {
        final InherentCO2MonitoringApproach inherentCO2MonitoringApproach =
            (InherentCO2MonitoringApproach) permit.getMonitoringApproaches().getMonitoringApproaches().get(getMonitoringApproachType());

        List<InherentReceivingTransferringInstallation> permitInherentReceivingTransferringInstallations =
            inherentCO2MonitoringApproach.getInherentReceivingTransferringInstallations();

        List<AerInherentReceivingTransferringInstallation> aerInherentReceivingTransferringInstallations = new ArrayList<>();

        permitInherentReceivingTransferringInstallations.forEach(inherentReceivingTransferringInstallation -> {
                AerInherentReceivingTransferringInstallation aerInherentReceivingTransferringInstallation = AerInherentReceivingTransferringInstallation.builder()
                    .id(UUID.randomUUID().toString())
                    .inherentReceivingTransferringInstallation(copyExceptReportableEmissions(inherentReceivingTransferringInstallation))
                    .build();
                aerInherentReceivingTransferringInstallations.add(aerInherentReceivingTransferringInstallation);
            }
        );

        return InherentCO2Emissions.builder()
            .type(getMonitoringApproachType())
            .inherentReceivingTransferringInstallations(aerInherentReceivingTransferringInstallations)
            .build();
    }

    private InherentReceivingTransferringInstallation copyExceptReportableEmissions(
        InherentReceivingTransferringInstallation inherentReceivingTransferringInstallation) {
        InherentReceivingTransferringInstallation copiedInherentReceivingTransferringInstallation = PERMIT_ENTITY_MAPPER.cloneApproach(
            inherentReceivingTransferringInstallation);
        copiedInherentReceivingTransferringInstallation.setTotalEmissions(null);
        return copiedInherentReceivingTransferringInstallation;
    }

    @Override
    public MonitoringApproachType getMonitoringApproachType() {
        return MonitoringApproachType.INHERENT_CO2;
    }
}
