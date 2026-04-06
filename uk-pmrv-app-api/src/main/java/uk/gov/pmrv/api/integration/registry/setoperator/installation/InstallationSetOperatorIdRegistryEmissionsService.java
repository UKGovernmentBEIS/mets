package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.InstallationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsEntity;
import uk.gov.pmrv.api.reporting.service.ReportableEmissionsService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationSetOperatorIdRegistryEmissionsService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ReportableEmissionsService reportableEmissionsService;

    public void notifyRegistryWithEmissions(Long accountId) {

        List<ReportableEmissionsEntity> reportableEmissionsEntities =
            reportableEmissionsService.getReportableEmissionsByAccountId(accountId);

        reportableEmissionsEntities.forEach(entity -> {
            InstallationReportableEmissionsUpdatedEvent event =
                    InstallationReportableEmissionsUpdatedEvent.builder()
                            .accountId(accountId)
                            .year(entity.getYear())
                            .reportableEmissions(entity.getReportableEmissions())
                            .isFromDre(entity.isFromDre())
                            .isSetOperatorId(true)
                            .build();

            applicationEventPublisher.publishEvent(event);
        });

    }
}
