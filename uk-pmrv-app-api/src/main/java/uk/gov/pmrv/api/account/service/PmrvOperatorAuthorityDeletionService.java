package uk.gov.pmrv.api.account.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityDeleteValidator;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityDeletionService;
import uk.gov.pmrv.api.account.domain.event.AccountContactRegistryEvent;

import java.util.List;

@Service
public class PmrvOperatorAuthorityDeletionService extends OperatorAuthorityDeletionService {

    private final ApplicationEventPublisher applicationEventPublisher;

    public PmrvOperatorAuthorityDeletionService(AuthorityRepository authorityRepository, List<OperatorAuthorityDeleteValidator> operatorAuthorityDeleteValidators, ApplicationEventPublisher eventPublisher,ApplicationEventPublisher applicationEventPublisher) {
        super(authorityRepository, operatorAuthorityDeleteValidators, eventPublisher);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public void deleteAccountOperatorAuthorityAndNotifyRegistry(String userId,Long accountId) {
        this.deleteAccountOperatorAuthority(userId, accountId);
        applicationEventPublisher.publishEvent(AccountContactRegistryEvent.builder().accountsIds(List.of(accountId)).build());
    }
}
