package uk.gov.pmrv.api.user.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.domain.UserLoginDomain;
import uk.gov.pmrv.api.user.core.repository.UserLoginDomainRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginDomainServiceTest {

    @InjectMocks
    private UserLoginDomainService userLoginDomainService;

    @Mock
    private UserLoginDomainRepository userLoginDomainRepository;

    @Test
    void registerUserLastLoginDomain_when_already_exists() {
        String userId = "userId";
        AccountType lastLoginDomain = AccountType.INSTALLATION;

        UserLoginDomain userLoginDomain = UserLoginDomain.builder()
            .userId(userId)
            .lastLoginDomain(AccountType.INSTALLATION)
            .build();

        when(userLoginDomainRepository.findById(userId)).thenReturn(Optional.of(userLoginDomain));

        userLoginDomainService.registerUserLastLoginDomain(userId, lastLoginDomain);

        assertEquals(lastLoginDomain, userLoginDomain.getLastLoginDomain());
    }

    @Test
    void registerUserLastLoginDomain_when_new() {
        String userId = "userId";
        AccountType lastLoginDomain = AccountType.INSTALLATION;

        when(userLoginDomainRepository.findById(userId)).thenReturn(Optional.empty());

        userLoginDomainService.registerUserLastLoginDomain(userId, lastLoginDomain);

        ArgumentCaptor<UserLoginDomain> userLoginDomainArgumentCaptor = ArgumentCaptor.forClass(UserLoginDomain.class);
        verify(userLoginDomainRepository, times(1)).save(userLoginDomainArgumentCaptor.capture());
        UserLoginDomain userLoginDomainCaptured = userLoginDomainArgumentCaptor.getValue();
        assertEquals(lastLoginDomain, userLoginDomainCaptured.getLastLoginDomain());
    }

    @Test
    void getUserLastLoginDomain_when_exists() {
        String userId = "userId";
        AccountType lastLoginDomain = AccountType.INSTALLATION;

        UserLoginDomain userLoginDomain = UserLoginDomain.builder()
            .userId(userId)
            .lastLoginDomain(lastLoginDomain)
            .build();

        when(userLoginDomainRepository.findById(userId)).thenReturn(Optional.of(userLoginDomain));

        assertEquals(lastLoginDomain, userLoginDomainService.getUserLastLoginDomain(userId));
    }

    @Test
    void getUserLastLoginDomain_when_not_exists() {
        String userId = "userId";

        when(userLoginDomainRepository.findById(userId)).thenReturn(Optional.empty());

        assertNull(userLoginDomainService.getUserLastLoginDomain(userId));
    }
}