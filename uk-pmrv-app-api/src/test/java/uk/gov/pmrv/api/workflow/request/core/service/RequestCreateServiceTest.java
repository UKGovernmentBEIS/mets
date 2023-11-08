package uk.gov.pmrv.api.workflow.request.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RequestCreateServiceTest {

    @InjectMocks
    private RequestCreateService service;

    @Mock
    private RequestRepository requestRepository;
    
    @Mock
    private AccountQueryService accountQueryService;
    
    @Test
    void createRequest_with_accountId() {
    	final RequestType type = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestStatus status = RequestStatus.IN_PROGRESS;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        final Long accountId = 1L;
        final Long verificationBodyId = 1L;

        when(accountQueryService.getAccountCa(accountId)).thenReturn(ca);
        when(accountQueryService.getAccountVerificationBodyId(accountId)).thenReturn(Optional.of(verificationBodyId));
    	
        RequestParams requestParams = RequestParams.builder()
            .requestId("1")
            .type(type)
            .accountId(accountId)
            .build();
        //invoke
        service.createRequest(requestParams, status);
        
        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository, times(1)).save(requestCaptor.capture());
        Request request = requestCaptor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.getType()).isEqualTo(type);
        assertThat(request.getStatus()).isEqualTo(status);
        assertThat(request.getCompetentAuthority()).isEqualTo(ca);
        assertThat(request.getVerificationBodyId()).isEqualTo(verificationBodyId);
        assertThat(request.getAccountId()).isEqualTo(accountId);

        verify(accountQueryService, times(1)).getAccountCa(accountId);
        verify(accountQueryService, times(1)).getAccountVerificationBodyId(accountId);
    }
    
    @Test
    void createRequest_with_comp_authority() {
    	final RequestType type = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestStatus status = RequestStatus.IN_PROGRESS;
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        RequestParams requestParams = RequestParams.builder()
            .requestId("1")
            .type(type)
            .competentAuthority(ca)
            .build();
        //invoke
        service.createRequest(requestParams, status);
        
        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository, times(1)).save(requestCaptor.capture());
        Request request = requestCaptor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.getType()).isEqualTo(type);
        assertThat(request.getStatus()).isEqualTo(status);
        assertThat(request.getCompetentAuthority()).isEqualTo(ca);
        assertThat(request.getVerificationBodyId()).isNull();

        verifyNoInteractions(accountQueryService);
    }
}
