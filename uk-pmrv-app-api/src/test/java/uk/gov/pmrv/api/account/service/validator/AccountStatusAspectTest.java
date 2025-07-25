package uk.gov.pmrv.api.account.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountStatusAspectTest {

    @Mock
    private AccountRepository accountRepository;
    
    private AccountValidTest test = new AccountValidTest();
    
    @BeforeEach
    void setUp() {
        AccountStatusAspect aspect = new AccountStatusAspect(accountRepository);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(test);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        test = (AccountValidTest) aopProxy.getProxy();
    }
    
    @Test
    void validateAccountStatus() {
        Long accountId = 1L;
        Account account = Mockito.mock(Account.class);

        when(account.getStatus()).thenReturn(TestAccountStatus.TEST_ACCOUNT_STATUS_A);
        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));
        
        test.test(accountId);
        
        verify(accountRepository, times(1)).findByIdForUpdate(accountId);
    }
    
    @Test
    void validateAccountStatus_throws_status_invalid_exception() {
        Long accountId = 1L;
        Account account = Mockito.mock(Account.class);

        when(account.getStatus()).thenReturn(TestAccountStatus.TEST_ACCOUNT_STATUS_B);
        when(accountRepository.findByIdForUpdate(accountId)).thenReturn(Optional.of(account));
        
        BusinessException exc = assertThrows(BusinessException.class, () -> test.test(accountId));
        assertThat(exc.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_INVALID_STATUS);
        
        verify(accountRepository, times(1)).findByIdForUpdate(accountId);
    }
    
    public static class AccountValidTest {
        @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'TEST_ACCOUNT_STATUS_A'}")
        public void test(Long accountId) {
            
        }
    }

    public enum TestAccountStatus implements uk.gov.pmrv.api.account.domain.enumeration.AccountStatus {
        TEST_ACCOUNT_STATUS_A,
        TEST_ACCOUNT_STATUS_B
        ;

        @Override
        public String getName() {
            return this.name();
        }
    }
}
