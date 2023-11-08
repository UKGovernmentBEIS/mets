package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service;

import java.util.Map;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.mapper.EmpVariationUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsDeemedWithdrawnAddRequestActionService {

	private final RequestService requestService;
	private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final EmpVariationUkEtsReviewMapper EMP_VARIATION_MAPPER = Mappers.getMapper(EmpVariationUkEtsReviewMapper.class);

    public void addRequestAction(final String requestId) {
        Request request = requestService.findRequestById(requestId);
        EmpVariationUkEtsRequestPayload requestPayload = (EmpVariationUkEtsRequestPayload) request.getPayload();
        
    	// get users' information
        DecisionNotification notification = requestPayload.getDecisionNotification();
        Map<String, RequestActionUserInfo> usersInfo =
            requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(), request);

        EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload requestActionPayload =
        		EMP_VARIATION_MAPPER.toEmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload(requestPayload,
        				usersInfo, RequestActionPayloadType.EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD);

        requestService.addActionToRequest(request,
            requestActionPayload,
            RequestActionType.EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN,
            requestPayload.getRegulatorReviewer());
    }
}
