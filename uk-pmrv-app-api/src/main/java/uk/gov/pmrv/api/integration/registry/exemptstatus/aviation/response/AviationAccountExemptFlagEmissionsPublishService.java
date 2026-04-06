package uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;

import java.time.Year;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.aviation.exempt.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountExemptFlagEmissionsPublishService {

    private final AviationReportableEmissionsService aviationReportableEmissionsService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEmissions(Long accountId, Year year) {
        aviationReportableEmissionsService.getReportableEmissionsForYear(accountId, year)
                .ifPresent(entity -> {
                    AviationReportableEmissionsUpdatedEvent event =
                            AviationReportableEmissionsUpdatedEvent.builder()
                                    .accountId(accountId)
                                    .year(entity.getYear())
                                    .reportableEmissions(entity.getReportableEmissions())
                                    .isFromDre(entity.isFromDre())
                                    .isHistorical(true)
                                    .build();

                    applicationEventPublisher.publishEvent(event);
                });
        }

}



