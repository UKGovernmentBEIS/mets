package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.mapper.DoalMapper;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DoalSubmitService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final DoalMapper DOAL_MAPPER = Mappers.getMapper(DoalMapper.class);

    @Transactional
    public void applySaveAction(final RequestTask requestTask, final DoalSaveApplicationRequestTaskActionPayload taskActionPayload) {
        final DoalApplicationSubmitRequestTaskPayload requestTaskPayload =
                (DoalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setDoal(taskActionPayload.getDoal());
        requestTaskPayload.setDoalSectionsCompleted(taskActionPayload.getDoalSectionsCompleted());
    }

    @Transactional
    public void notifyOperator(RequestTask requestTask,
                               final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {
        Request request = requestTask.getRequest();
        DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();
        final DoalApplicationSubmitRequestTaskPayload taskPayload =
                (DoalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Update request
        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);
        requestPayload.setDecisionNotification(taskActionPayload.getDecisionNotification());
        updateRequestPayload(requestPayload, taskPayload);
    }

    @Transactional
    public void complete(RequestTask requestTask) {
        Request request = requestTask.getRequest();
        DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();
        final DoalApplicationSubmitRequestTaskPayload taskPayload =
                (DoalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Update request
        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);
        updateRequestPayload(requestPayload, taskPayload);
    }

    @Transactional
    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
        Request request = requestTask.getRequest();
        DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();
        final DoalApplicationSubmitRequestTaskPayload taskPayload =
                (DoalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Update request
        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        updateRequestPayload(requestPayload, taskPayload);

        // Add request action
        requestService.addActionToRequest(request,
                null,
                RequestActionType.DOAL_APPLICATION_PEER_REVIEW_REQUESTED,
                appUser.getUserId());
    }

    @Transactional
    public void addProceededToAuthorityRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();

        DoalApplicationProceededToAuthorityRequestActionPayload actionPayload = DOAL_MAPPER
                .toDoalApplicationProceededToAuthorityRequestActionPayload(requestPayload);

        // Add users info if decision notification exists
        final DecisionNotification notification = requestPayload.getDecisionNotification();
        if(notification != null) {
            final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
                    .getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
            actionPayload.setUsersInfo(usersInfo);
        }

        // Add to request
        requestService.addActionToRequest(
                request,
                actionPayload,
                RequestActionType.DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY,
                requestPayload.getRegulatorAssignee());
    }

    private void updateRequestPayload(DoalRequestPayload requestPayload, DoalApplicationSubmitRequestTaskPayload taskPayload) {
        requestPayload.setDoal(taskPayload.getDoal());
        requestPayload.setDoalSectionsCompleted(taskPayload.getDoalSectionsCompleted());
        requestPayload.setDoalAttachments(taskPayload.getDoalAttachments());
    }
}
