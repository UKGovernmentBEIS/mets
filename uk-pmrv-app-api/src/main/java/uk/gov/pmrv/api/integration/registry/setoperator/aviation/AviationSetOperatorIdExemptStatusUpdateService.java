package uk.gov.pmrv.api.integration.registry.setoperator.aviation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountReportingStatusRepository;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptFlagEvent;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class AviationSetOperatorIdExemptStatusUpdateService {

    private final AviationAccountReportingStatusRepository aviationAccountReportingStatusRepository;
    private final ApplicationEventPublisher publisher;


    @Transactional
    public void notifyRegistryWithExemptStatuses(Account account) {
        List<AviationAccountReportingStatus> aviationAccountReportingStatuses =
                aviationAccountReportingStatusRepository.findByAccountIdOrderByYearDesc(account.getId());

        aviationAccountReportingStatuses.forEach(aviationAccountReportingStatus -> {
            AviationAccountExemptFlagEvent aviationAccountExemptFlagEvent =
                AviationAccountExemptFlagEvent.builder()
                        .year(aviationAccountReportingStatus.getYear())
                        .isExempt(!AviationAccountReportingStatusType.REQUIRED_TO_REPORT
                                .equals(aviationAccountReportingStatus.getStatus()))
                        .registryId(account.getRegistryId())
                        .accountId(account.getId())
                        .build();

            publisher.publishEvent(aviationAccountExemptFlagEvent);
        });
    }
}
