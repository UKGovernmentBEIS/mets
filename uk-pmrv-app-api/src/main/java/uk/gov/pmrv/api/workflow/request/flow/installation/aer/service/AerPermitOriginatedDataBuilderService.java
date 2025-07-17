package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.MonitoringApproachSourceStreamMonitoringTiersMapper;

@Service
@RequiredArgsConstructor
public class AerPermitOriginatedDataBuilderService {

    private final AerRequestQueryService aerRequestQueryService;

    public PermitOriginatedData buildPermitOriginatedData(Long accountId, Aer aer,
                                                               PermitContainer permitContainer,
                                                               InstallationCategory installationCategory) {
        return PermitOriginatedData.builder()
            .permitMonitoringApproachMonitoringTiers(
                MonitoringApproachSourceStreamMonitoringTiersMapper
                    .transformToMonitoringApproachMonitoringTiers(aer.getMonitoringApproachEmissions())
            )
            .permitNotificationIds(aerRequestQueryService.getApprovedPermitNotificationIdsByAccount(accountId))
            .permitType(permitContainer.getPermitType())
            .installationCategory(installationCategory)
            .build();
    }
}
