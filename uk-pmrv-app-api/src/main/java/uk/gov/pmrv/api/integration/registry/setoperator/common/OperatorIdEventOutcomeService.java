package uk.gov.pmrv.api.integration.registry.setoperator.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.integration.registry.accountcreated.common.validation.SetOperatorIdResponseValidator;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseStatus;

import java.util.List;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class OperatorIdEventOutcomeService {

    private final AccountRepository accountRepository;
    private final SetOperatorIdResponseValidator setOperatorIdResponseValidator;

    @Transactional
    public SetOperatorIdEventOutcome getOperatorIdEventOutcome(SetOperatorIdResponseEvent event) {
        List<RegistryIntegrationEventError> errors = setOperatorIdResponseValidator.validate(event);
        SetOperatorIdEventOutcome eventOutcome = SetOperatorIdEventOutcome.builder().event(event).errors(errors).build();
        if(errors.isEmpty()) {
            Account account = accountRepository.findAccountByEmitterId(event.getEmitterId())
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
            account.setRegistryId(event.getOperatorId());
            accountRepository.save(account);
            eventOutcome.setOutcome(RegistryResponseStatus.SUCCESS);
        }
        else {
            eventOutcome.setOutcome(RegistryResponseStatus.ERROR);
        }
        return eventOutcome;
    }

    public SetOperatorIdEventOutcome getInternalErrorEventOutcome(SetOperatorIdResponseEvent event) {
        return SetOperatorIdEventOutcome.builder()
                .errors(List.of(RegistryIntegrationEventError.builder().error(RegistryResponseErrorCode.ERROR_0200)
                        .errorMessage(RegistryResponseErrorCode.ERROR_0200.getDescription()).build()))
                .event(event).outcome(RegistryResponseStatus.ERROR).build();
    }

}
