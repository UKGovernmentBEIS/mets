package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRSaveAuthorityResponseTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper.ALRMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ALRAuthorityResponseService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private static final ALRMapper ALR_MAPPER = Mappers.getMapper(ALRMapper.class);

    @Transactional
    public void applyAuthorityResponseSaveAction(final RequestTask requestTask,
                                                 final ALRSaveAuthorityResponseTaskActionPayload actionPayload) {

        final ALRAuthorityResponseSubmitRequestTaskPayload taskPayload =
                (ALRAuthorityResponseSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setAuthorityReviewOutcome(actionPayload.getAuthorityReviewOutcome());
        taskPayload.setAuthorityReviewSectionsCompleted(actionPayload.getAuthorityReviewSectionsCompleted());
    }

    @Transactional
    public void authorityResponseNotifyOperator(RequestTask requestTask,
                                                final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {
        Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();
        final ALRAuthorityResponseSubmitRequestTaskPayload taskPayload =
                (ALRAuthorityResponseSubmitRequestTaskPayload) requestTask.getPayload();

        // Update request
        requestPayload.setDecisionNotification(taskActionPayload.getDecisionNotification());
        requestPayload.setAuthorityReviewOutcome(taskPayload.getAuthorityReviewOutcome());
        requestPayload.setAlrSectionsCompleted(taskPayload.getAuthorityReviewSectionsCompleted());
        requestPayload.setAlrAttachments(taskPayload.getAlrAttachments());
    }

    @Transactional
    public void addSubmittedRequestAction(final String requestId, RequestActionType actionType) {
        final Request request = requestService.findRequestById(requestId);
        final ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();

        ALRAuthorityResponseSubmittedRequestActionPayload actionPayload = ALR_MAPPER
                .toALRAuthorityResponseSubmittedRequestActionPayload(requestPayload, actionType);

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
