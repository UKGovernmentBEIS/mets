package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.mapper.DreMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DreAddSubmittedRequestActionService {

	private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
	private static final DreMapper DRE_MAPPER = Mappers.getMapper(DreMapper.class);
	
	@Transactional
	public void add(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final DreRequestPayload requestPayload = (DreRequestPayload) request.getPayload();

		final DreApplicationSubmittedRequestActionPayload actionPayload = DRE_MAPPER.toSubmittedActionPayload(requestPayload);

		final DecisionNotification notification = requestPayload.getDecisionNotification();
		final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
				.getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
		actionPayload.setUsersInfo(usersInfo);
            
		requestService.addActionToRequest(request, 
				actionPayload, 
				RequestActionType.DRE_APPLICATION_SUBMITTED,
				requestPayload.getRegulatorAssignee());
	}
}
