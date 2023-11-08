package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import java.util.Map;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper.PermitVariationReviewRequestActionMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationAddGrantedRequestActionService {

	private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	private static final PermitVariationReviewRequestActionMapper permitVariationReviewRequestActionMapper = Mappers
			.getMapper(PermitVariationReviewRequestActionMapper.class);
	
	@Transactional
	public void add(final String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();

		final PermitVariationApplicationGrantedRequestActionPayload actionPayload = permitVariationReviewRequestActionMapper
				.toPermitVariationApplicationGrantedRequestActionPayload(requestPayload);

		final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
				.getInstallationOperatorDetails(request.getAccountId());
		actionPayload.setInstallationOperatorDetails(installationOperatorDetails);

		final DecisionNotification notification = requestPayload.getDecisionNotification();
		final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
				.getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
		actionPayload.setUsersInfo(usersInfo);
            
		requestService.addActionToRequest(request, 
				actionPayload, 
				RequestActionType.PERMIT_VARIATION_APPLICATION_GRANTED,
				requestPayload.getRegulatorReviewer());
	}
}
