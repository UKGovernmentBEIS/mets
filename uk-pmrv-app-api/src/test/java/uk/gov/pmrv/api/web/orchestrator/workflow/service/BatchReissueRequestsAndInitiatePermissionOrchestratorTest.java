package uk.gov.pmrv.api.web.orchestrator.workflow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.orchestrator.workflow.dto.BatchReissuesResponseDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestHistoryCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchReissueRequestsAndInitiatePermissionOrchestratorTest {

	@InjectMocks
    private BatchReissueRequestsAndInitiatePermissionOrchestrator cut;

    @Mock
    private RequestQueryService requestQueryService;
    
    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    
    @Test
    void findBatchReissueRequests() {
    	AccountType accountType = AccountType.INSTALLATION;
    	AppUser authUser = AppUser.builder().userId("userId")
    			.authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build())).build(); 
    	PagingRequest pagingRequestInfo = PagingRequest.builder().pageNumber(0L).pageSize(30L).build();
    	
    	RequestDetailsSearchResults requestDetailsSearchResults = RequestDetailsSearchResults.builder()
    			.total(10L)
    			.requestDetails(List.of(new RequestDetailsDTO("requestId", RequestType.PERMIT_BATCH_REISSUE, RequestStatus.COMPLETED, LocalDateTime.now(), PermitBatchReissueRequestMetadata.builder()
        								.submitter("submitter")
        								.build())))
    			.build();
    
		RequestSearchCriteria requestSearchCriteria = RequestSearchCriteria.builder()
				.resourceType(ResourceType.CA)
				.resourceId(authUser.getCompetentAuthority().name())
				.category(RequestHistoryCategory.CA)
				.requestTypes(Set.of(RequestType.PERMIT_BATCH_REISSUE))
				.paging(pagingRequestInfo).build();
    	
    	when(requestQueryService.findRequestDetailsBySearchCriteria(requestSearchCriteria)).thenReturn(requestDetailsSearchResults);
    	when(compAuthAuthorizationResourceService.hasUserScopeOnResourceSubType(authUser,
				Scope.REQUEST_CREATE, RequestType.PERMIT_BATCH_REISSUE.name())).thenReturn(true);
    	
    	BatchReissuesResponseDTO result = cut.findBatchReissueRequests(authUser, accountType, pagingRequestInfo);
    	
    	assertThat(result).isEqualTo(BatchReissuesResponseDTO.builder()
    			.requestDetailsSearchResults(requestDetailsSearchResults)
				.canInitiateBatchReissue(true)
				.build());
    	
    	verify(requestQueryService, times(1)).findRequestDetailsBySearchCriteria(requestSearchCriteria);
    	verify(compAuthAuthorizationResourceService, times(1)).hasUserScopeOnResourceSubType(authUser,
				Scope.REQUEST_CREATE, RequestType.PERMIT_BATCH_REISSUE.name());
    	
    }
}
