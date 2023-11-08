package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.domain.UserLoginDomain;
import uk.gov.pmrv.api.user.core.repository.UserLoginDomainRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLoginDomainService {

    private final UserLoginDomainRepository userLoginDomainRepository;

    @Transactional
    public void registerUserLastLoginDomain(String userId, AccountType lastLoginDomain) {
        Optional<UserLoginDomain> userLoginDomainOptional = userLoginDomainRepository.findById(userId);

        userLoginDomainOptional.ifPresentOrElse(
            userLoginDomain -> {
                userLoginDomain.setLastLoginDomain(lastLoginDomain);
            },
            () -> createUserLoginDomain(userId, lastLoginDomain)
        );
    }

    @Transactional
    public void deleteByUserId(String userId) {
        // in case the user has never logged in, there is no entry in the user login domain table
        if (userLoginDomainRepository.existsById(userId)) {
            userLoginDomainRepository.deleteById(userId);   
        }
    }

    public AccountType getUserLastLoginDomain(String userId) {
        return userLoginDomainRepository.findById(userId).map(UserLoginDomain::getLastLoginDomain).orElse(null);
    }

    private void createUserLoginDomain(String userId, AccountType lastLoginDomain) {
        userLoginDomainRepository.save(
            UserLoginDomain.builder()
                .userId(userId)
                .lastLoginDomain(lastLoginDomain)
                .build()
        );
    }
}
