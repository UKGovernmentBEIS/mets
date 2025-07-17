package uk.gov.pmrv.api.web.orchestrator.workflow.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.orchestrator.workflow.dto.BatchReissuesResponseDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestHistoryCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper.AccountTypeBatchReissueRequestTypeMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BatchReissueRequestsAndInitiatePermissionOrchestrator {

	private final RequestQueryService requestQueryService;
	private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
	private static final AccountTypeBatchReissueRequestTypeMapper accountTypeBatchReissueRequestTypeMapper = Mappers
			.getMapper(AccountTypeBatchReissueRequestTypeMapper.class);
	
	public BatchReissuesResponseDTO findBatchReissueRequests(AppUser authUser, AccountType accountType, PagingRequest pagingRequestInfo) {
		final RequestType batchReissueRequestType = accountTypeBatchReissueRequestTypeMapper.accountTypeToBatchReissueRequestType(accountType);
		final RequestDetailsSearchResults requestDetailsSearchResults = requestQueryService.findRequestDetailsBySearchCriteria(RequestSearchCriteria.builder()
				.resourceType(ResourceType.CA)
				.resourceId(authUser.getCompetentAuthority().name())
				.category(RequestHistoryCategory.CA)
				.requestTypes(Set.of(batchReissueRequestType))
				.paging(pagingRequestInfo)
				.build());
		final boolean canInitiateBatchReissue = compAuthAuthorizationResourceService.hasUserScopeOnResourceSubType(authUser,
				Scope.REQUEST_CREATE, batchReissueRequestType.name());
		
		return BatchReissuesResponseDTO.builder()
				.requestDetailsSearchResults(requestDetailsSearchResults)
				.canInitiateBatchReissue(canInitiateBatchReissue)
				.build();
	}
}
