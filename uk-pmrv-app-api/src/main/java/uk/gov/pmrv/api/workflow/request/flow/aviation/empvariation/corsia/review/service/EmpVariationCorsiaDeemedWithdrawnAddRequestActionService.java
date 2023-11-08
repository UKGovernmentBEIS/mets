package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service;

import java.util.Map;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.mapper.EmpVariationCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaDeemedWithdrawnAddRequestActionService {

	private final RequestService requestService;
	private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final EmpVariationCorsiaReviewMapper MAPPER = Mappers.getMapper(EmpVariationCorsiaReviewMapper.class);

    public void addRequestAction(final String requestId) {
        Request request = requestService.findRequestById(requestId);
        EmpVariationCorsiaRequestPayload requestPayload = (EmpVariationCorsiaRequestPayload) request.getPayload();
        
        // get users' information
        DecisionNotification notification = requestPayload.getDecisionNotification();
        Map<String, RequestActionUserInfo> usersInfo =
            requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(), request);

        EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload requestActionPayload =
        		MAPPER.toEmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload(requestPayload,
        				usersInfo, RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD);

        requestService.addActionToRequest(request,
            requestActionPayload,
            RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN,
            requestPayload.getRegulatorReviewer());
    }
}
