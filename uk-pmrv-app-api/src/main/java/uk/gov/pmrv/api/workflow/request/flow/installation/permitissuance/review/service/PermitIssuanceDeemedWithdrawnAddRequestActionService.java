package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.mapper.PermitReviewMapper;

@Service
@RequiredArgsConstructor
public class PermitIssuanceDeemedWithdrawnAddRequestActionService {
    
    private static final PermitReviewMapper PERMIT_REVIEW_MAPPER = Mappers.getMapper(PermitReviewMapper.class);
    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    
    public void addRequestAction(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();

        final RequestActionType requestActionType = RequestActionType.valueOf(request.getType() + "_APPLICATION_DEEMED_WITHDRAWN");
        final RequestActionPayloadType requestActionPayloadType = RequestActionPayloadType.valueOf(request.getType() + "_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD");

        final PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload actionPayload =
            PERMIT_REVIEW_MAPPER.toPermitIssuanceApplicationDeemedWithdrawnRequestActionPayload(requestPayload, requestActionPayloadType);

        // get users' information
        final DecisionNotification notification =
            ((PermitIssuanceRequestPayload) request.getPayload()).getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo =
            requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(),
                request);
        actionPayload.setUsersInfo(usersInfo);

        final String reviewer = requestPayload.getRegulatorReviewer();

        requestService.addActionToRequest(request,
            actionPayload,
            requestActionType,
            reviewer);
    }
}
