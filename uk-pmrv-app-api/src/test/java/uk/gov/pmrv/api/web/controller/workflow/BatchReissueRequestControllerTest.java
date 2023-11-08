package uk.gov.pmrv.api.web.controller.workflow;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.workflow.service.BatchReissueRequestsAndInitiatePermissionOrchestrator;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.web.orchestrator.workflow.dto.BatchReissuesResponseDTO;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;

@ExtendWith(MockitoExtension.class)
class BatchReissueRequestControllerTest {

	private static final String BASE_PATH = "/v1.0/%s/batch-reissue-requests";

    private MockMvc mockMvc;

    @InjectMocks
    private BatchReissueRequestController controller;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private BatchReissueRequestsAndInitiatePermissionOrchestrator batchReissueRequestsAndInitiatePermissionOrchestrator;
    
    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (BatchReissueRequestController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getBatchReissueRequests() throws Exception {
    	final PmrvUser authUser = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
    	final AccountType accountType = AccountType.INSTALLATION;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        
        final BatchReissuesResponseDTO response = BatchReissuesResponseDTO.builder()
        		.requestDetailsSearchResults(RequestDetailsSearchResults.builder()
        				.total(10L)
        				.requestDetails(List.of(
        						new RequestDetailsDTO("requestId", RequestType.PERMIT_BATCH_REISSUE, RequestStatus.COMPLETED, LocalDateTime.now(), PermitBatchReissueRequestMetadata.builder()
        								.submitter("submitter")
        								.build())
        						)).build())
        		.canInitiateBatchReissue(true)
                .build();
        
        PagingRequest paging = PagingRequest.builder()
					.pageNumber(0L)
					.pageSize(1L)
					.build();
        
        when(batchReissueRequestsAndInitiatePermissionOrchestrator.findBatchReissueRequests(authUser, accountType, paging)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format(BASE_PATH, AccountType.INSTALLATION) + "?page=0&size=1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requestDetails[0].id").value("requestId"))
            .andExpect(jsonPath("$.total").value(10L))
            .andExpect(jsonPath("$.canInitiateBatchReissue").value(true))
            
            ;

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(batchReissueRequestsAndInitiatePermissionOrchestrator, times(1)).findBatchReissueRequests(authUser, accountType, paging);
    }
    
    @Test
    void getBatchReissueRequests_forbidden() throws Exception {
    	final PmrvUser authUser = PmrvUser.builder().roleType(RoleType.OPERATOR).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(authUser, new RoleType[]{REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
            .get(String.format(BASE_PATH, AccountType.INSTALLATION) + "?page=0&size=1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(batchReissueRequestsAndInitiatePermissionOrchestrator);
    }
}
