package uk.gov.pmrv.api.integration.registry.setoperator.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.validation.SetOperatorIdResponseValidator;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.InstallationAccountUpdatedNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.InstallationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;

import java.util.List;
import java.util.Optional;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class OperatorIdEventOutcomeService {

    private final AccountRepository accountRepository;
    private final SetOperatorIdResponseValidator setOperatorIdResponseValidator;
    private final Optional<InstallationAccountUpdatedNotifyRegistryService> registryService;

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
            registryService.ifPresent(service ->
                    service.notifyRegistry(InstallationAccountUpdatedRegistryEvent.builder()
                            .accountId(account.getId()).build())
            );
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
