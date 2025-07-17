package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper.PermitVariationReviewRequestActionMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermitVariationAddDeemedWithdrawnRequestActionService {
	
	private final RequestService requestService;
	private final RequestActionUserInfoResolver requestActionUserInfoResolver;
	private static final PermitVariationReviewRequestActionMapper PERMIT_VARIATION_REQUEST_ACTION_MAPPER = Mappers
			.getMapper(PermitVariationReviewRequestActionMapper.class);
	

	@Transactional
	public void add(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
        final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();

        final PermitVariationApplicationDeemedWithdrawnRequestActionPayload actionPayload =
        		PERMIT_VARIATION_REQUEST_ACTION_MAPPER.toPermitVariationApplicationDeemedWithdrawnRequestActionPayload(requestPayload);

        // get users' information
        final DecisionNotification notification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo =
            requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(),
                request);
        actionPayload.setUsersInfo(usersInfo);

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN,
            requestPayload.getRegulatorReviewer());
	}
}
