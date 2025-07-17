package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalSaveAuthorityResponseTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.mapper.DoalMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DoalAuthorityResponseService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final DoalMapper DOAL_MAPPER = Mappers.getMapper(DoalMapper.class);

    @Transactional
    public void applyAuthorityResponseSaveAction(final RequestTask requestTask,
                                                 final DoalSaveAuthorityResponseTaskActionPayload actionPayload) {

        final DoalAuthorityResponseRequestTaskPayload taskPayload =
                (DoalAuthorityResponseRequestTaskPayload) requestTask.getPayload();

        taskPayload.setDoalAuthority(actionPayload.getDoalAuthority());
        taskPayload.setDoalSectionsCompleted(actionPayload.getDoalSectionsCompleted());
    }

    @Transactional
    public void authorityResponseNotifyOperator(RequestTask requestTask,
                               final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {
        Request request = requestTask.getRequest();
        DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();
        final DoalAuthorityResponseRequestTaskPayload taskPayload =
                (DoalAuthorityResponseRequestTaskPayload) requestTask.getPayload();

        // Update request
        requestPayload.setDecisionNotification(taskActionPayload.getDecisionNotification());
        requestPayload.setDoalAuthority(taskPayload.getDoalAuthority());
        requestPayload.setDoalSectionsCompleted(taskPayload.getDoalSectionsCompleted());
        requestPayload.setDoalAttachments(taskPayload.getDoalAttachments());
    }

    @Transactional
    public void addSubmittedRequestAction(final String requestId, RequestActionType actionType) {
        final Request request = requestService.findRequestById(requestId);
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();

        DoalAuthorityResponseSubmittedRequestActionPayload actionPayload = DOAL_MAPPER
                .toDoalAuthorityResponseSubmittedRequestActionPayload(requestPayload, actionType);

        // Add users info
        final DecisionNotification notification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                .getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
        actionPayload.setUsersInfo(usersInfo);

        // Add to request
        requestService.addActionToRequest(
                request,
                actionPayload,
                actionType,
                requestPayload.getRegulatorAssignee());
    }
}
