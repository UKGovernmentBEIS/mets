package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@ExtendWith(MockitoExtension.class)
class RequestAccountContactQueryServiceTest {

    @InjectMocks
    private RequestAccountContactQueryService service;

    @Mock
    private AccountContactQueryService accountContactQueryService;
    
    @Mock
    private UserAuthService userAuthService;
    
    @Test
    void getRequestAccountContact() {
        Long accountId = 1L;
        Request request = Request.builder().accountId(accountId).build();
        AccountContactType contactType = AccountContactType.PRIMARY;
        
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("email@email")
                .build();
        
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, contactType))
            .thenReturn(Optional.of("primaryUserId"));
        
        when(userAuthService.getUserByUserId("primaryUserId")).thenReturn(userInfoDTO);
        
        //invoke
        Optional<UserInfoDTO> result = service.getRequestAccountContact(request, contactType);
            
        assertThat(result).contains(userInfoDTO);
        
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, contactType);
        verify(userAuthService, times(1)).getUserByUserId("primaryUserId");
    }
    
    @Test
    void getRequestAccountPrimaryContact() {
        Long accountId = 1L;
        Request request = Request.builder().accountId(accountId).build();
        
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("email@email")
                .build();
        
        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY))
            .thenReturn(Optional.of("primaryUserId"));
        
        when(userAuthService.getUserByUserId("primaryUserId")).thenReturn(userInfoDTO);
        
        //invoke
        Optional<UserInfoDTO> result = service.getRequestAccountPrimaryContact(request);
            
        assertThat(result).contains(userInfoDTO);
        
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY);
        verify(userAuthService, times(1)).getUserByUserId("primaryUserId");
    }
    
    @Test
    void getRequestAccountPrimaryContact_not_found() {
        Long accountId = 1L;
        Request request = Request.builder().accountId(accountId).build();

        when(accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY))
            .thenReturn(Optional.empty());

        Optional<UserInfoDTO> result = service.getRequestAccountPrimaryContact(request);

        //invoke
        assertFalse(result.isPresent());
        verify(accountContactQueryService, times(1)).findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY);
        verifyNoInteractions(userAuthService);
    }
    
    
}
