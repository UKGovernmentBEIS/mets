package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETICompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper.HSETIMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HSETICompleteService {

    private final RequestService requestService;
    private static final HSETIMapper HSETI_MAPPER = Mappers.getMapper(HSETIMapper.class);
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    public void addApprovedRequestAction(final String requestId) {
        addRequestAction(requestId, RequestActionType.HSE_TI_APPROVED);
    }

    public void addRejectedRequestAction(final String requestId) {
        addRequestAction(requestId, RequestActionType.HSE_TI_REJECTED);
    }

    public void addWithdrawnRequestAction(final String requestId) {
        addRequestAction(requestId, RequestActionType.HSE_TI_WITHDRAWN);
    }

    public void addDeemedWithdrawnRequestAction(final String requestId) {
        addRequestAction(requestId, RequestActionType.HSE_TI_DEEMED_WITHDRAWN);
    }


    private void addRequestAction(String requestId, RequestActionType actionType) {

        final Request request = requestService.findRequestById(requestId);
        final HSETIRequestPayload requestPayload = (HSETIRequestPayload) request.getPayload();

        final HSETICompletedRequestActionPayload actionPayload =
                HSETI_MAPPER.toHSETICompletedRequestActionPayload(requestPayload);

        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                .getUsersInfo(
                        actionPayload.getDecisionNotification().getOperators(),
                        actionPayload.getDecisionNotification().getSignatory(),
                        request);

        actionPayload.setUsersInfo(usersInfo);

        actionPayload.setHsetiAttachments(requestPayload.getHsetiAttachments());
        actionPayload.setRegulatorReviewAttachments(requestPayload.getRegulatorReviewAttachments());


        requestService.addActionToRequest(request,
                actionPayload,
                actionType,
                requestPayload.getRegulatorReviewer());
    }
}
