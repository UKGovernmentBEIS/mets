package uk.gov.pmrv.api.integration.registry.setoperator.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.event.AccountContactRegistryEvent;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.validation.SetOperatorIdResponseValidator;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.setoperator.aviation.AviationSetOperatorIdAccountUpdateService;
import uk.gov.pmrv.api.integration.registry.setoperator.aviation.AviationSetOperatorIdExemptStatusUpdateService;
import uk.gov.pmrv.api.integration.registry.setoperator.installation.InstallationSetOperatorIdRegistryEmissionsService;
import uk.gov.pmrv.api.integration.registry.setoperator.installation.InstallationSetOperatorIdWithholdFlagUpdateService;

import java.util.List;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class OperatorIdEventOutcomeService {

    private final AccountRepository accountRepository;
    private final SetOperatorIdResponseValidator setOperatorIdResponseValidator;
    private final AviationSetOperatorIdExemptStatusUpdateService aviationSetOperatorIdExemptStatusUpdateService;
    private final ApplicationEventPublisher publisher;
    private final AviationSetOperatorIdAccountUpdateService aviationSetOperatorIdAccountUpdateService;
    private final InstallationSetOperatorIdWithholdFlagUpdateService installationSetOperatorIdWithholdFlagUpdateService;
    private final InstallationSetOperatorIdRegistryEmissionsService installationSetOperatorIdRegistryEmissionsService;


    @Transactional
    public OperatorUpdateEventOutcome getAviationOperatorIdEventOutcome(OperatorUpdateEvent event) {
        List<IntegrationEventErrorDetails> errors = setOperatorIdResponseValidator.validateAviation(event);
        OperatorUpdateEventOutcome eventOutcome = OperatorUpdateEventOutcome.builder().event(event).errors(errors).build();
        if(errors.isEmpty()) {
            Account account = accountRepository.findAccountByEmitterId(event.getEmitterId())
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
            account.setRegistryId(event.getOperatorId().intValue());
            accountRepository.save(account);
            eventOutcome.setOutcome(IntegrationEventOutcome.SUCCESS);
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.AVIATION_SERVICE_KEY, event.getEmitterId(),
                    NotifyRegistryUtils.OPERATOR_ID_INTEGRATION_POINT_KEY, "Operator Id received from registry " + event);
            aviationSetOperatorIdAccountUpdateService.notifyRegistryWithAccountUpdate(account.getId());
            publisher.publishEvent(AccountContactRegistryEvent.builder().accountsIds(List.of(account.getId())).build());
            aviationSetOperatorIdExemptStatusUpdateService.notifyRegistryWithExemptStatuses(account);
        }
        else {
            eventOutcome.setOutcome(IntegrationEventOutcome.ERROR);
        }
        return eventOutcome;
    }

    @Transactional
    public OperatorUpdateEventOutcome getInstallationOperatorIdEventOutcome(OperatorUpdateEvent event) {
        List<IntegrationEventErrorDetails> errors = setOperatorIdResponseValidator.validateInstallation(event);
        OperatorUpdateEventOutcome eventOutcome = OperatorUpdateEventOutcome.builder().event(event).errors(errors).build();
        if(errors.isEmpty()) {
            Account account = accountRepository.findAccountByEmitterId(event.getEmitterId())
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
            account.setRegistryId(event.getOperatorId().intValue());
            accountRepository.save(account);
            eventOutcome.setOutcome(IntegrationEventOutcome.SUCCESS);
            log.info(REQUEST_LOG_FORMAT, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY, event.getEmitterId(),
                    NotifyRegistryUtils.OPERATOR_ID_INTEGRATION_POINT_KEY, "Operator Id received from registry " + event);
            publisher.publishEvent(InstallationAccountUpdatedRegistryEvent.builder().accountId(account.getId()).isFromSetOperatorId(true).build());
            publisher.publishEvent(AccountContactRegistryEvent.builder().accountsIds(List.of(account.getId())).build());
            installationSetOperatorIdWithholdFlagUpdateService.notifyRegistryWithWithholdFlag(account.getId());
            installationSetOperatorIdRegistryEmissionsService.notifyRegistryWithEmissions(account.getId());
        }
        else {
            eventOutcome.setOutcome(IntegrationEventOutcome.ERROR);
        }
        return eventOutcome;
    }

    public OperatorUpdateEventOutcome getInternalErrorEventOutcome(OperatorUpdateEvent event) {
        return OperatorUpdateEventOutcome.builder()
                .errors(List.of(IntegrationEventErrorDetails.builder().error(IntegrationEventError.ERROR_0200)
                        .errorMessage(RegistryResponseErrorCode.ERROR_0200.getDescription()).build()))
                .event(event).outcome(IntegrationEventOutcome.ERROR).build();
    }

}
