package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.event.FirstPrimaryContactAssignedToAccountEvent;
import uk.gov.pmrv.api.account.domain.event.FirstServiceContactAssignedToAccountEvent;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountContactTypeUpdateValidator;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountContactUpdateService {

    private final AccountRepository accountRepository;
    private final AuthorityService authorityService;
    private final List<AccountContactTypeUpdateValidator> contactTypeValidators;
    private final ApplicationEventPublisher eventPublisher;

    private final AccountContactQueryService accountContactQueryService;

    @Transactional
    public void assignUserAsDefaultAccountContactPoint(String user, Account account) {
        account.getContacts().put(AccountContactType.PRIMARY, user);
        account.getContacts().put(AccountContactType.SERVICE, user);
        account.getContacts().put(AccountContactType.FINANCIAL, user);

        accountRepository.save(account);
    }

    @Transactional
    public void updateAccountContacts(Map<AccountContactType, String> updatedContactTypes, Long accountId) {
        validateUpdatedContactTypeUsers(updatedContactTypes.values(), accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Map<AccountContactType, String> currentContactTypes = account.getContacts().isEmpty()? new EnumMap<>(AccountContactType.class) : new EnumMap<>(account.getContacts());
        Map<AccountContactType, String> allContactTypes =
                constructAllContactTypes(currentContactTypes, updatedContactTypes, accountId);

        //validate
        contactTypeValidators.forEach(v -> v.validateUpdate(allContactTypes, accountId));

        //save
        account.getContacts().putAll(allContactTypes);

        // If currentContactTypes is empty means that contacts are assigned for first time in the account.
        // This should only happen when a regulator user activates the first operator admin of an aviation account.
        String primaryContact = allContactTypes.get(AccountContactType.PRIMARY);
        publishFirstPrimaryContactAssignedToAccountEvent(currentContactTypes, primaryContact, accountId);

        String serviceContact = allContactTypes.get(AccountContactType.SERVICE);
        publishFirstServiceContactAssignedToAccountEvent(currentContactTypes, serviceContact, accountId);
    }

    private void publishFirstPrimaryContactAssignedToAccountEvent(Map<AccountContactType, String> currentContactTypes, String primaryContact, Long accountId) {
        if (currentContactTypes.isEmpty() && primaryContact != null) {
            eventPublisher.publishEvent(FirstPrimaryContactAssignedToAccountEvent.builder()
                    .accountId(accountId)
                    .userId(primaryContact)
                    .build());
        }
    }

    private void publishFirstServiceContactAssignedToAccountEvent(Map<AccountContactType, String> currentContactTypes, String serviceContact, Long accountId) {
        if (currentContactTypes.isEmpty() && serviceContact != null) {
            final Optional<ServiceContactDetails> serviceContactDetailsOpt = accountContactQueryService.getServiceContactDetails(accountId);
            final ServiceContactDetails serviceContactDetails = serviceContactDetailsOpt.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
            eventPublisher.publishEvent(FirstServiceContactAssignedToAccountEvent.builder()
                    .accountId(accountId)
                    .serviceContactDetails(serviceContactDetails)
                    .build());
        }
    }

    private void validateUpdatedContactTypeUsers(Collection<String> users, Long accountId) {
        users.forEach(u -> validateUpdatedContactTypeUser(u, accountId));
    }

    private void validateUpdatedContactTypeUser(String user, Long accountId) {
        if (user == null) {
            return;
        }

        if (!authorityService.existsByUserIdAndAccountId(user, accountId)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, user);
        }
    }

    private Map<AccountContactType, String> constructAllContactTypes(Map<AccountContactType, String> currentContactTypes,
                                                                     Map<AccountContactType, String> updatedContactTypes,
                                                                     Long accountId) {
        Map<AccountContactType, String> notUpdatedContactTypes = new EnumMap<>(AccountContactType.class);
        currentContactTypes.forEach((key, value) -> {
            if (!updatedContactTypes.containsKey(key)) {
                notUpdatedContactTypes.put(key, value);
            }
        });

        Map<AccountContactType, String> allContactTypes = new EnumMap<>(AccountContactType.class);
        allContactTypes.putAll(updatedContactTypes);
        allContactTypes.putAll(notUpdatedContactTypes);

        final Map<String, AuthorityStatus> userStatuses = this.getUserStatuses(accountId, allContactTypes);

        nullifyValuesForNonActiveUsers(allContactTypes, userStatuses);
        return allContactTypes;
    }

    private Map<String, AuthorityStatus> getUserStatuses(final Long accountId,
                                                         final Map<AccountContactType, String> allContactTypes) {

        final Set<AccountContactType> operatorContactTypes = AccountContactType.getOperatorAccountContactTypes();

        final List<String> operatorUsers = allContactTypes.entrySet().stream()
                .filter(e -> operatorContactTypes.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        final List<String> nonOperatorUsers = allContactTypes.entrySet().stream()
                .filter(e -> !operatorContactTypes.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        final Map<String, AuthorityStatus> operatorStatuses = authorityService.findStatusByUsersAndAccountId(operatorUsers, accountId);
        final Map<String, AuthorityStatus> nonOperatorStatuses = nonOperatorUsers.isEmpty() ?
                Map.of() : authorityService.findStatusByUsers(nonOperatorUsers);

        return Stream.concat(operatorStatuses.entrySet().stream(), nonOperatorStatuses.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void nullifyValuesForNonActiveUsers(
            Map<AccountContactType, String> allContactTypes, Map<String, AuthorityStatus> userStatuses) {
        allContactTypes.entrySet().forEach(e -> {
            String user = e.getValue();
            if (user != null &&
                    userStatuses.get(user) != AuthorityStatus.ACTIVE) {
                e.setValue(null);
            }
        });
    }
}
