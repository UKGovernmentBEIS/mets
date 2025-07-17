package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationEndedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NerMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NerApplicationAddDeemedWithdrawnRequestActionService {

    private static final NerMapper NER_MAPPER = Mappers.getMapper(NerMapper.class);

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    public void deemWithdrawn(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        final String assignee = request.getPayload().getRegulatorAssignee();

        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        final NerApplicationEndedRequestActionPayload actionPayload =
            NER_MAPPER.toNerApplicationEnded(requestPayload);

        final DecisionNotification notification =
            requestPayload.getDecisionNotification();

        final Map<String, RequestActionUserInfo> usersInfo =
            requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(),
                request);
        actionPayload.setUsersInfo(usersInfo);

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.NER_APPLICATION_DEEMED_WITHDRAWN,
            assignee);
    }
}
